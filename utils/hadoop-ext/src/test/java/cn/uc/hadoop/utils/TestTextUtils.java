package cn.uc.hadoop.utils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.nio.charset.CharacterCodingException;
import java.util.Random;

import org.apache.hadoop.io.Text;
import org.junit.Test;

public class TestTextUtils {
	private	String s = "真的不错啊abcd";
	private	String s1 = s;
	private	String s2 = "真的不错啊";
	private	String s3 = "abcd";
	private	char c = '真';
	private	char c1 = c;
	private	char c2 = 'a';
	 static abstract class SimpleBenchMark{
		protected abstract String getName();
		protected abstract String test1(int number);
		protected abstract String test2(int number);
		protected String test3(int number){
			return null;
		}
		public void runBeanchMark(int number){
			long begin = System.nanoTime();
			String t1 =test1(number);
			long end = System.nanoTime();
			long u1 = end-begin;
			begin = System.nanoTime();
			String t2 =test2(number);
			end = System.nanoTime();
			long u2 = end-begin;
			begin = System.nanoTime();
			String t3 =test3(number);
			end = System.nanoTime();
			long u3 = end-begin;
			System.out.println("testing:"+getName()+" for "+number+" times");
			System.out.println("" + t1 +" using "+ u1/1000 + " ms" );
			System.out.println("" + t2 +" using "+ u2/1000 + " ms" );
			if(t3!=null) System.out.println("" + t3 +" using "+ u3/1000 + " ms" );
			if( t3!=null && u3<u1 && u3<u2 ){
				System.out.println("resutl is :"+t3+" fastest .");
			}
			else{
				if(u1<u2) System.out.println("resutl is :"+t1+" faster ");
				else System.out.println("resutl is :"+t2+" faster ");
			}
			System.out.println(" ");
		}
	}
	private Text getLessByteText(String a){
		return getLessByteText(a,a);
	}
	private Text getLessByteText(String a,String more){
		Text text = new Text(a+more);
		text.set(a);
		return text;
	}
	private static String generateString(Random rng, String characters, int length)
	{
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}
	@Test
	public void testEncode() {
		try {
			byte[] bs = TextUtils.encode(s);
			byte[] bs2 = TextUtils.encode(s2);
			byte[] bs3 = TextUtils.encode(s3);
			byte[] cs = TextUtils.encode(c);
			byte[] cs2 = TextUtils.encode(c2);

			String ts = new String(TextUtils.encode(s));
			assertTrue(ts.equals(s));
			// 测试 ‘真’ 这个字符是否对应
			boolean same = true;
			for (int i = 0; i < cs.length; i++) {
				if (bs[i] != cs[i]) {
					System.out.println(bs[i] + " " + cs[i]);
					same = false;
					break;
				}
			}
			assertTrue(same);
			//测试字节数是否相同
			assertTrue(bs.length==bs2.length+bs3.length);
			//测试跟string的getbyte相同
			byte[] temp = s.getBytes();
			assertTrue(temp.length==bs.length);
			same = true;
			for (int i = 0; i < bs.length; i++) {
				if (bs[i] != temp[i]) {
					System.out.println(bs[i] + " " + temp[i]);
					same = false;
					break;
				}
			}
			assertTrue(same);
			
			// 测试 ‘a’ 这个字符是否在s2之后
			assertTrue(cs2.length == 1);
			assertTrue(bs[bs2.length] == cs2[0]);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Test
	public void testAppend() {
		

		try {
			Text t1 = new Text(s);
			TextUtils.append(t1,s,s2,s3);
			
			Text t2 = getLessByteText(s);
			TextUtils.append(t2,s,s2,s3);
			
			byte[] x = (s+s+s2+s3).getBytes();
			Text t3 = new Text(x);
			
			assertTrue(x.length==t1.getLength());
			assertTrue(t1.equals(t3));
			
			assertTrue(t1.getLength() == t2.getLength());
			assertTrue(t1.equals(t2));
			
			Text t4 = new Text(s);
			TextUtils.append(t4,c,c2);
			
			Text t5 = new Text((s+c+c2).getBytes());
			
			assertTrue(t4.getLength() == t5.getLength());
			assertTrue(t4.equals(t5));
			
			
			Text t6 = new Text(s);
			TextUtils.append(t6,s.getBytes(),s2.getBytes(),s3.getBytes());
			
			Text t7 = getLessByteText(s);
			TextUtils.append(t7,s.getBytes(),s2.getBytes(),s3.getBytes());
			
			byte[] xxx = (s+s+s2+s3).getBytes();
			Text t8 = new Text(xxx);
			
			
			assertTrue(xxx.length==t6.getLength());
			assertTrue(t6.equals(t8));
			
			assertTrue(t7.equals(t8));
			
			Text t10 = new Text("真的");
			Text t11 = this.getLessByteText("挺快");
			Text t12 = new Text("的啊");
			TextUtils.append(t10, t11,t12);
			assertTrue(t10.equals(new Text("真的挺快的啊")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testFind(){
		try{
			byte[] b1 = s1.getBytes();
			byte[] b2 = s2.getBytes();
			assertTrue(TextUtils.find(new Text(s1), s2)==0);
			
			assertTrue(TextUtils.find(new Text(s1), 'a')==b2.length);
			
			assertTrue(TextUtils.find(new Text(s1), 'z')==-1);
			
			assertTrue(TextUtils.find(new Text(), 'a')==-1);
			
			assertTrue(TextUtils.find(this.getLessByteText(s1,"z"), 'z')==-1);
			
			//寻找第N个
			assertTrue(TextUtils.find(this.getLessByteText(s1,s1), 'a',1)==b2.length);
			
			Text t = this.getLessByteText(s1+s1,s1);
			assertTrue(TextUtils.find(t, 'a',2)==b2.length+b1.length);
			
			//特殊字符串
			String s1="a,,bcd,e,f,g";
			Text t1 = new Text(s1);
			assertTrue(TextUtils.find(t1,',',2)==2);
			assertTrue(TextUtils.find(t1,',',4)=="a,,bcd,e".length());
			assertTrue(TextUtils.find(t1,',',6)==-1);
			
			//特殊字符串
			String s2=",,,,,";
			Text t2 = new Text(s2);
			assertTrue(TextUtils.find(t2,',',2)==1);
			assertTrue(TextUtils.find(t2,',',4)==3);
			assertTrue(TextUtils.find(t1,',',7)==-1);
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStartsWith(){
		try{
			assertTrue(TextUtils.startsWith(new Text(s1),s1.substring(0,1)));
			assertTrue(TextUtils.startsWith(new Text(s1),s1.substring(0,1).getBytes()));
			assertTrue(TextUtils.startsWith(new Text(s1),c1));
			
			assertTrue(TextUtils.startsWith(this.getLessByteText(s1),s1.substring(0,1)));
			assertTrue(TextUtils.startsWith(this.getLessByteText(s1),s1.substring(0,1).getBytes()));
			assertTrue(TextUtils.startsWith(this.getLessByteText(s1),c1));
			
			assertFalse(TextUtils.startsWith(new Text(s1),s3));
			assertFalse(TextUtils.startsWith(new Text(s1),s3.getBytes()));
			assertFalse(TextUtils.startsWith(new Text(s1),c2));
			
			assertFalse(TextUtils.startsWith(new Text(s3),s1));
			
			assertTrue(TextUtils.startsWith(new Text(s1),s1));
			assertTrue(TextUtils.startsWith(new Text(""),""));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEndsWith(){
		try{
			assertTrue(TextUtils.endsWith(new Text(s1),"abcd"));
			assertTrue(TextUtils.endsWith(new Text(s1),"abcd".getBytes()));
			assertTrue(TextUtils.endsWith(new Text(s1),"d"));
			
			assertFalse(TextUtils.endsWith(new Text(s1),"真心"));
			assertFalse(TextUtils.endsWith(new Text(s1),"真心".getBytes()));
			assertFalse(TextUtils.endsWith(new Text(s1),"c"));
			
			assertTrue(TextUtils.endsWith(new Text(s1),s1));
			assertTrue(TextUtils.endsWith(new Text(""),""));
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//below is beachmark
	public void testEncodeBeanchMark(){
		SimpleBenchMark sbm = new SimpleBenchMark(){
			protected String getName() {
				return "encode test";
			}
			protected String test1(int number) {
				for(int i=0;i<number;i++){
					s.getBytes();
				}
				return "String.getBytes()";
			}

			@Override
			protected String test2(int number) {
				for(int i=0;i<number;i++){
					try {
						TextUtils.encode(s);
					} catch (CharacterCodingException e) {
						e.printStackTrace();
					}
				}
				return "TextUtils.encode()";
			}
		};
		sbm.runBeanchMark(1000);
	}
	
	public void testAppendBeanchMark(){
		SimpleBenchMark sbm = new SimpleBenchMark(){
			protected String getName() {
				return "append test";
			}
			protected String test1(int number) {
//				Random random = new Random();
//				String s1 = generateString(random, "abcdefgh",10);
//				String s2 = generateString(random, "abcdefgh",10);
//				String s3 = generateString(random, "abcdefgh",10);
				for(int i=0;i<number;i++){
					String temp = s1+s2+s3;
				}
				return "String + ";
			}
			protected String test2(int number) {
				byte[] b1 = s1.getBytes();
				byte[] b2 = s2.getBytes();
				byte[] b3 = s3.getBytes();
				for(int i=0;i<number;i++){
					Text t = new Text();
					TextUtils.append(t,b1,b2,b3);
				}
				return "TextUtils.append()";
			}
			protected String test3(int number) {
				for(int i=0;i<number;i++){
					StringBuilder sb = new StringBuilder();
					sb.append(s1).append(s2).append(s3);
					String temp =sb.toString();
				}
				return "Stringbuilder  ";
			}
		};
		sbm.runBeanchMark(1000);
	}
	
//	@Test
	public void beanchMark(){
//		testEncodeBeanchMark();
		testAppendBeanchMark();
	}
	public static void main(String[] arga){
		TestTextUtils ttu = new TestTextUtils();
		ttu.beanchMark();
	}
}