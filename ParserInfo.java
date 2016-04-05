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
			// �������ӳ�ʱΪ5��
			httpConn.setConnectTimeout(5000);
			// ���ö�ȡ���ݳ�ʱΪ30��
			httpConn.setReadTimeout(30000);
			// HttpURLConnection.setFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			// ����httpЭ��ͷ
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/4.0(compatible; MSIE 6.0; Windows 2000)");

			
			
			// �ڴ˴�������ؽ����OKΪ���سɹ���Not FoundΪ����ʧ��
			returnResult = httpConn.getResponseMessage();
			// System.out.println(returnResult);

			if ("OK".equals(returnResult)) {
				
				in = httpConn.getInputStream();

				// ��ȡ����
				charset = ParserUtils.getCharsetFormUrl(urlStr);
				// System.out.println("��ǰҳ��ı����ʽΪ��" + charset);
				// ���ֽ�����װ���ַ���
				br = new BufferedReader(new InputStreamReader(in, charset));

				// ������ʽ���
				while ((tmpString = br.readLine()) != null) {
					PageContent += tmpString+"\n";
				}
			} 
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("����ʧ��");
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
			System.out.println("...........���title����");
			
			metaTag = ParserUtils.parseTag(PageContent, MetaTag.class, "name", "description");
			if(metaTag==null)
				throw new Exception();
			//System.out.println(metaTag.getMetaContent());
			//output.write(metaTag.getMetaContent()+"\r\n");
			result+=metaTag.getMetaContent()+"\r\n";
			System.out.println("...........���description����");
			
			metaTag = ParserUtils.parseTag(PageContent, MetaTag.class, "name", "keywords");
			if(metaTag==null)
				throw new Exception();
			//System.out.println(metaTag.getMetaContent());
			//output.write(metaTag.getMetaContent()+"\r\n");
			result+=metaTag.getMetaContent()+"\r\n";
			System.out.println("...........���Keywords����");
		
			if(!result.matches("\\s*"))
			{
				count++;
				// ��������ҳ����ļ�����
				File dir = new File("datas/"+name);
				
				if (dir != null && !dir.exists()) {
					dir.mkdirs();
				}
				
				// ��������ҳ����ļ���
				movieName = movieName.substring(0, movieName.indexOf('(')-1);
				File file = new File("datas/"+name+"/"+name+".txt");

				file.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(
						file));
				
				System.out.println(movieName+"�������");
				System.out.println("Ŀǰ������أ�"+count);
				
				//remove control characteristics
				movieName = movieName.replaceAll("&nbsp;", " ");				
				movieName = movieName.replaceAll("&ldquo;", "��");
				movieName = movieName.replaceAll("&rdquo;", "��");
				movieName = movieName.replaceAll("&lt;", "<");
				movieName = movieName.replaceAll("&gt;", ">");
				movieName = movieName.replaceAll("&amp;", "&");
				movieName = movieName.replaceAll("&quot;", "\"");
				movieName = movieName.replaceAll("&\\w{2,8};", " ");
				
				output.write(movieName+"\r\n");
				// дurl
				output.write(urlStr+"\r\n");
				
				//remove control characteristics
				result = result.replaceAll("&nbsp;", " ");				
				result = result.replaceAll("&ldquo;", "��");
				result = result.replaceAll("&rdquo;", "��");
				result = result.replaceAll("&lt;", "<");
				result = result.replaceAll("&gt;", ">");
				result = result.replaceAll("&amp;", "&");
				result = result.replaceAll("&quot;", "\"");
				result = result.replaceAll("&\\w{2,8};", " ");
				
				// д�����ļ�����
				output.write(result);
				
				output.close();
				
				//����ͼƬ
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