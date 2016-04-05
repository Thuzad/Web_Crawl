import java.io.File;

public class Download
{
	public static void startDownlaod(int index, int num){
		final int ind = index;
		for(;index < ind+num; index++)
		{
			String number = String.format("%07d", index);
			Download.download(number);
		}
	}
	
	/*public static void main(String[] args){
			Download.download("1300854");
	}*/
	
	public static void download(String number){
		File dir = new File("datas");
		
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		
		String url = "http://www.imdb.com/title/tt"+number+"/?ref_=inth_ov_tt";
		ParserInfo.parseInfo(url, number);
	}
}