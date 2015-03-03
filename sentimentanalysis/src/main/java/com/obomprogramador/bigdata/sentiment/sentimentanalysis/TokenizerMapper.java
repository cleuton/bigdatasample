package com.obomprogramador.bigdata.sentiment.sentimentanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import com.obomprogramador.nlp.SentiWordNetDemoCode;

public class TokenizerMapper 
       extends Mapper<Object, Text, Text, IntWritable>{
    
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    private List<String> linhas;
    private SentiWordNetDemoCode sdc;
      
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	
    	if (this.linhas == null) {
    		getSentiFile(context);
    	}
        
    	String senti = sdc.analyze(value.toString()).toString();
    	word.set(senti);
    	context.write(word, one);
    }
    
	private void getSentiFile(Context context) throws IOException {
    	Configuration conf = context.getConfiguration();
    	String swnPath = conf.get("sentwordnetfile");
    	System.out.println("@@@ Path: " + swnPath);
    	this.linhas = new ArrayList<String>();
        try{
	        Path pt=new Path(swnPath);
	        FileSystem fs = FileSystem.get(new Configuration());
	        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
	        String line;
	        line=br.readLine();
	        while (line != null){
	                linhas.add(line);
	                line=br.readLine();
	        }
		}catch(Exception e){
			System.out.println("@@@@ ERRO: " + e.getMessage());
			throw new IOException(e);
		}   
        sdc = new SentiWordNetDemoCode(linhas);
	}
}