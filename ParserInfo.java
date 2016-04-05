import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.htmlparser.tags.MetaTag;

public class ParserInfo
{
	private static int count;
	
	public static void parseInfo(String urlStr, String name){
		String tmpString = null;
		String returnResult = null;
		String charset = null;
		String PageContent = "";
		String result = "";
		String movieName = null;
		URL url = null;
		HttpURLConnection httpConn = null;
		
		InputStream in = null;
		BufferedReader br = null;
		MetaTag metaTag = null;
		
		try {						
			url = new URL(urlStr);
			httpConn = (HttpURLConnection) url.openConnection();
			// 设置链接超时为5秒
			httpConn.setConnectTimeout(5000);
			// 设置读取数据超时为30秒
			httpConn.setReadTimeout(30000);
			// HttpURLConnection.setFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			// 设置http协议头
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/4.0(compatible; MSIE 6.0; Windows 2000)");

			
			
			// 在此处输出返回结果，OK为返回成功，Not Found为返回失败
			returnResult = httpConn.getResponseMessage();
			// System.out.println(returnResult);

			if ("OK".equals(returnResult)) {
				
				in = httpConn.getInputStream();

				// 获取编码
				charset = ParserUtils.getCharsetFormUrl(urlStr);
				// System.out.println("当前页面的编码格式为：" + charset);
				// 将字节流封装成字符流
				br = new BufferedReader(new InputStreamReader(in, charset));

				// 正则表达式完成
				while ((tmpString = br.readLine()) != null) {
					PageContent += tmpString+"\n";
				}
			} 
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("下载失败");
			e.printStackTrace();
		} finally {
			try {
				if ("OK".equals(returnResult)) {
					if(br != null)
						br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}				
		
		
		try {
			
			
			try{
				String temp = ParserUtils.parseScore(PageContent);
				if(temp != null && !temp.matches("\\s*"))
					result+=temp+"\r\n";
				else
					result+="?\r\n";
			} catch(Exception e){
				//e.printStackTrace();
				result+="?\r\n";
			}
			
			
			metaTag = ParserUtils.parseTag(PageContent, MetaTag.class, "name", "title");
			//System.out.println(metaTag.getMetaContent());
			//output.write(metaTag.getMetaContent()+"\r\n");
			if(metaTag==null)
				throw new Exception();
			movieName = metaTag.getMetaContent();
			result+=metaTag.getMetaContent()+"\r\n";
			System.out.println("...........完成title下载");
			
			metaTag = ParserUtils.parseTag(PageContent, MetaTag.class, "name", "description");
			if(metaTag==null)
				throw new Exception();
			//System.out.println(metaTag.getMetaContent());
			//output.write(metaTag.getMetaContent()+"\r\n");
			result+=metaTag.getMetaContent()+"\r\n";
			System.out.println("...........完成description下载");
			
			metaTag = ParserUtils.parseTag(PageContent, MetaTag.class, "name", "keywords");
			if(metaTag==null)
				throw new Exception();
			//System.out.println(metaTag.getMetaContent());
			//output.write(metaTag.getMetaContent()+"\r\n");
			result+=metaTag.getMetaContent()+"\r\n";
			System.out.println("...........完成Keywords下载");
		
			if(!result.matches("\\s*"))
			{
				count++;
				// 设置下载页面的文件夹名
				File dir = new File("datas/"+name);
				
				if (dir != null && !dir.exists()) {
					dir.mkdirs();
				}
				
				// 设置下载页面的文件名
				movieName = movieName.substring(0, movieName.indexOf('(')-1);
				File file = new File("datas/"+name+"/"+name+".txt");

				file.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(
						file));
				
				System.out.println(movieName+"下载完毕");
				System.out.println("目前完成下载："+count);
				
				//remove control characteristics
				movieName = movieName.replaceAll("&nbsp;", " ");				
				movieName = movieName.replaceAll("&ldquo;", "“");
				movieName = movieName.replaceAll("&rdquo;", "”");
				movieName = movieName.replaceAll("&lt;", "<");
				movieName = movieName.replaceAll("&gt;", ">");
				movieName = movieName.replaceAll("&amp;", "&");
				movieName = movieName.replaceAll("&quot;", "\"");
				movieName = movieName.replaceAll("&\\w{2,8};", " ");
				
				output.write(movieName+"\r\n");
				// 写url
				output.write(urlStr+"\r\n");
				
				//remove control characteristics
				result = result.replaceAll("&nbsp;", " ");				
				result = result.replaceAll("&ldquo;", "“");
				result = result.replaceAll("&rdquo;", "”");
				result = result.replaceAll("&lt;", "<");
				result = result.replaceAll("&gt;", ">");
				result = result.replaceAll("&amp;", "&");
				result = result.replaceAll("&quot;", "\"");
				result = result.replaceAll("&\\w{2,8};", " ");
				
				// 写具体文件内容
				output.write(result);
				
				output.close();
				
				//下载图片
				try
				{
					String imageStr = ParserUtils.parseImage(PageContent);
					if(imageStr != null)
					{
						URL imageUrl = new URL(imageStr);
						int end = imageStr.lastIndexOf(".");
						if(end > 0)
						{
							File imageFile = new File("datas/"+name+"/"+"pic"+imageStr.substring(end));
							InputStream is = imageUrl.openStream();
							OutputStream os = new FileOutputStream(imageFile);
							byte[] buff = new byte[1024];
							while(true){
								int readed = is.read(buff);
								if(readed == -1)
									break;
								byte[] temp = new byte[readed];
								System.arraycopy(buff, 0, temp, 0, readed);
								os.write(temp);
							}
							
							is.close();
							os.close();
						}						
					}
				} catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
								
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
}