//package com.ssafy.tnt;
//
//import javax.naming.Context;
//import java.io.IOException;
//import java.util.StringTokenizer;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.util.GenericOptionsParser;
//
//
//
//public class FileWriteRead {
//    /*
//	Object, Text : input key-value pair type (always same (to get a line of input file))
//	Text, IntWritable : output key-value pair type
//	*/
//    public static class TokenizerMapper
//            extends Mapper<Object, Text,Text,IntWritable> {
//
//        // variable declairations
//        private final static IntWritable one = new IntWritable(1);
//        private Text word = new Text();
//
//        // map function (Context -> fixed parameter)
//        public void map(Object key, Text value, Context context)
//                throws IOException, InterruptedException {
//
//            // value.toString() : get a line
//            StringTokenizer itr = new StringTokenizer(value.toString());
//            while ( itr.hasMoreTokens() ) {
//                word.set(itr.nextToken());
//
//                // emit a key-value pair
//                context.write(word,one);
//            }
//        }
//    }
//
//    /*
//    Text, IntWritable : input key type and the value type of input value list
//    Text, IntWritable : output key-value pair type
//    */
//    public static class IntSumReducer
//            extends Reducer<Text, IntWritable,Text,IntWritable> {
//
//        // variables
//        private IntWritable result = new IntWritable();
//
//        // key : a disticnt word
//        // values :  Iterable type (data list)
//        public void reduce(Text key, Iterable<IntWritable> values, Context context)
//                throws IOException, InterruptedException {
//
//            int sum = 0;
//            for ( IntWritable val : values ) {
//                sum += val.get();
//            }
//            result.set(sum);
//            context.write(key,result);
//        }
//    }
//}
