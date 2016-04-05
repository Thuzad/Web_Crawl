import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class ParserUtils {

	/**
	 * ��ȡ����ĳ������ֵ�ı�ǩ�б�
	 * @param html ����ȡ��html�ı�
	 * @param tagType ��ǩ������
	 * @param attributeName ĳ�����Ե�����
	 * @param attributeValue ����Ӧȡ��ֵ
	 * @return
	 */
	public static String parseScore(String PageContent)throws Exception
	{		
		Parser parser = new Parser();
		parser.setInputHTML(PageContent);
		
		NodeList ScoreTags = parser.parse(new HasAttributeFilter("class", "titlePageSprite star-box-giga-star"));

		Node it = (Node)ScoreTags.elementAt(0);
		//System.out.println(it.toPlainTextString());
		return it.toPlainTextString();
	}
	
	public static String parseImage(String PageContent) throws Exception
	{
		Parser parser = new Parser();
		parser.setInputHTML(PageContent);
		//NodeFilter filter1 = new
		//NodeFilter filter = new AndFilter(new NodeClassFilter(ImageTag.class), new HasAttributeFilter("itemprop", "image"));
		//NodeList imageTags = parser.parse(filter);
		NodeList imageTags = parser.parse(new NodeClassFilter(ImageTag.class));
		for(int i=0; i<imageTags.size(); i++)
		{
			ImageTag it = (ImageTag)imageTags.elementAt(i);
			if(it.getAttribute("itemprop") != null)
			{
				return it.getImageURL();				
			}
		}
		return null;
	}
	
	public static <T extends TagNode> List<T> parseTags(String html,final Class<T> tagType,final String attributeName,final String attributeValue){
		try {
			//����һ��html������
			Parser parser=new Parser();
			parser.setInputHTML(html);
			
			NodeList tagList = parser.parse(
					new NodeFilter(){
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public boolean accept(Node node) {
							if(node.getClass()==tagType){
								@SuppressWarnings("unchecked")
								T tn=(T) node;
								String attrValue=tn.getAttribute(attributeName);
								if(attrValue!=null && attrValue.equals(attributeValue)){
									return true;
								}
							}
							return false;
						}
					}
			);
			List<T> tags=new ArrayList<T>();
			for(int i=0; i<tagList.size();i++){
				@SuppressWarnings("unchecked")
				T t=(T) tagList.elementAt(i);
				tags.add(t);
			}
			return tags;
		} catch (ParserException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * ��ȡ����ĳ������ֵ�ı�ǩ
	 * @param html ����ȡ��html�ı�
	 * @param tagType ��ǩ������
	 * @param attributeName ĳ�����Ե�����
	 * @param attributeValue ����Ӧȡ��ֵ
	 * @return
	 */
	public static <T extends TagNode> T parseTag(String html,final Class<T> tagType,final String attributeName,final String attributeValue){
		List<T> tags=parseTags(html, tagType, attributeName, attributeValue);
		if(tags!=null&&tags.size()>0){
			return tags.get(0);
		}
		return null;
	}
	
	/**
	 * ��ȡ����ĳ������ֵ�ı�ǩ
	 * @param html ����ȡ��html�ı�
	 * @param tagType ��ǩ������
	 * @return
	 */
	public static <T extends TagNode> T parseTag(String html,final Class<T> tagType){
		return parseTag(html, tagType,null,null);
	}
	
	/**
	 * ��ȡ����ĳ������ֵ�ı�ǩ�б�
	 * @param html ����ȡ��html�ı�
	 * @param tagType ��ǩ������
	 * @return
	 */
	public static <T extends TagNode> List<T>  parseTags(String html,final Class<T> tagType){
		return parseTags(html, tagType,null,null);
	}
	
	public static String getCharsetFormUrl(String urlString) {
		InputStream in = null;
		int chByte = 0;
		URL url = null;
		HttpURLConnection httpConn = null;
		String contents = null;
		String charset = "utf-8";
		int len = 0;
		// �����ô���Ѿ����Զ��������ʽ��
		byte[] b = new byte[1024];
		try {
			url = new URL(urlString);
			httpConn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/4.0(compatible; MSIE 6.0; Windows 2000)");
			httpConn.setConnectTimeout(5000);
			httpConn.setReadTimeout(30000);
			// System.out.println(httpConn.getResponseMessage());
			in = httpConn.getInputStream();

			// ���ڱ������
			chByte = in.read();
			while (chByte != -1) {
				chByte = in.read();
				b[len++] = (byte) chByte;
				if (len >= 1024) {
					break;
				}
			}
			contents = new String(b);
			Pattern p = Pattern.compile(
					"<meta[^>]*?charset=[\"]?(\\w+)[\\W]*?>",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(contents);
			if (m.find()) {
				charset = m.group(1).trim();
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(charset);
		return charset;
	}
}
