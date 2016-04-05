public class ParserData implements Runnable
{
	public static void main(String[] args){
		for(int i=0; i < 10; i++){
			Thread demo = new Thread(new ParserData(i*1073+1375665, 1073));
			demo.start();
			System.out.println("start");
		}
	}
	
	private int index;
	private int num;
	
	public ParserData(int index, int num){
		this.index = index;
		this.num = num;
	}
	
	public void run(){
		Download.startDownlaod(index, num);
	}
}