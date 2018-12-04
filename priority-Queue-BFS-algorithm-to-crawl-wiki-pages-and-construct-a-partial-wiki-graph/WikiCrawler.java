/**
 * Crawling and Constructing Web Graph
 * Name: 
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;
import java.net.*;


public class WikiCrawler
{
    static final String BASE_URL = "https://en.wikipedia.org";
    
    private String seed;
    private int max;
    private String[] topics;
    private String output;
    
    private int numRequests;
    
    /**
     * Default constructor
     * @param seed: related address of seed URL (within wiki domain)
     * @param max: maximum number of pages to consider
     * @param topics: array of strings representing keywords in a topic-list
     * @param output: string representing the filename where the web graph
     *    over discovered pages are written.
     */
    public WikiCrawler(String seed, int max,
            String[] topics, String output)
    {
        this.seed = seed;
        this.max = max;
        this.topics = topics;
        this.output = output;
        
        numRequests = 0;
    }    
    
    /**
     * returns a list of strings consisting of links from the document
     * @param document
     * @return wiki links
     */
    public ArrayList<String> extractLinks(String document)
    {
        ArrayList<String> links = new ArrayList<String>();
        int pos = document.indexOf("<p>");
        int pos2 = document.indexOf("<P>");
        if (pos < 0 || (pos2 >= 0 && pos2 < pos))
            pos = pos2;
        if (pos < 0)
            pos = 0;
        
        pos = document.indexOf("\"/wiki/", pos);
        while (pos >= 0)
        {
            pos2 = document.indexOf("\"", pos + 1);
            if (pos2 >= 0)
            {
                String lk = document.substring(pos+1, pos2);
                if (lk.indexOf(":") < 0 && lk.indexOf("#") < 0 &&
                        !links.contains(lk))
                {
                    links.add(lk);
                }
                pos = pos2;
            }
            else
                pos++;
            
            pos = document.indexOf("\"/wiki/", pos);
        }
        
        return links;
    }
    
    /**
     * Get the priority value
     * @param document
     * @return int value
     */
    private int relevance(String document)
    {
        int cnt = 0;
        int pos = document.indexOf("<p>");
        int pos2 = document.indexOf("<P>");
        if (pos < 0 || (pos2 >= 0 && pos2 < pos))
            pos = pos2;
        if (pos < 0)
            pos = 0;
        
        for (int i = 0; i < topics.length; i++)
        {
            String s = topics[i];
            
            int idx = document.indexOf(s, pos);
            while (idx >= 0)
            {
                cnt++;
                idx = document.indexOf(s, idx + s.length());
            }            
        }
        
        return cnt;
    }
    
    /**
     * crawls/explores the web pages starting from the seed URL.
     * @param focused
     */
    public void crawl(boolean focused)
    {
        try
        {
            if (!focused || topics.length == 0)
            {
                bfs();
                return;
            }
            

            ArrayList<String> discovered = new ArrayList<String>();
            PriorityQ queue = new PriorityQ();
            ArrayList<String> lines = new ArrayList<String>();
            ArrayList< ArrayList<String> > lkarr = new ArrayList< ArrayList<String> > ();
            
            String doc = request(seed);
            queue.add(seed, relevance(doc));
            discovered.add(seed);
            lkarr.add(extractLinks(doc));
            int used = 1;
            
            PrintWriter pw = new PrintWriter(output);
            
            while (!queue.isEmpty())
            {
                String curr = queue.extractMax();
                int idx = discovered.indexOf(curr);                
                
                ArrayList<String> links = lkarr.get(idx);
                
                for (String v : links)
                {
                    if (!v.equals(curr) && (discovered.contains(v) || used < max))
                    {
                        lines.add(curr + " " + v);

                        if (!discovered.contains(v))
                        {
                            doc = request(v);
                            queue.add(v, relevance(doc));
                            lkarr.add(extractLinks(doc));
                            
                            discovered.add(v);
                            used++;
                        }
                    }              
                }
            }
            pw.println(discovered.size());
            for (String l : lines)
                pw.println(l);
            
            pw.close();
            
        } catch (IOException e)
        {
            
        }
    }
    
    /**
     * Perform the bfs search
     * @throws IOException 
     */
    private void bfs() throws IOException
    {
        ArrayList<String> discovered = new ArrayList<String>();
        LinkedList<String> queue = new LinkedList<String>();
        ArrayList<String> lines = new ArrayList<String>();
        
        queue.add(seed);
        discovered.add(seed);
        int used = 1;
        
        PrintWriter pw = new PrintWriter(output);
        
        while (!queue.isEmpty())
        {
            String curr = queue.poll();
            String doc = request(curr);            
            
            ArrayList<String> links = extractLinks(doc);
            
            for (String v : links)
            {
                if (!v.equals(curr) && (discovered.contains(v) || used < max))
                {
                    lines.add(curr + " " + v);

                    if (!discovered.contains(v))
                    {
                        queue.add(v);
                        discovered.add(v);
                        used++;
                    }
                }              
            }
        }
        pw.println(discovered.size());
        for (String l : lines)
            pw.println(l);
        
        pw.close();
    }
    
    /**
     * Send a web request
     * @param link
     * @return document
     */
    private String request(String link)
    {
        try
        {
            if (numRequests >= 20)
            {
                Thread.sleep(3500);
                numRequests = 0;
            }
            
            
            URL url = new URL(BASE_URL+link);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            StringBuffer buf = new StringBuffer();
            String line = br.readLine();
            while (line != null)
            {
                buf.append(line);
                buf.append("\n");
                line = br.readLine();
            }
            br.close();
            return buf.toString();
            
        } catch (Exception e)
        {
            return "";
        }
    }
    
    public static void main(String[] args)
    {
        WikiCrawler wc = new WikiCrawler("/wiki/Complexity_theory", 100, 
                new String[]{}, "test.txt");
        wc.crawl(false);
 
    }
}
