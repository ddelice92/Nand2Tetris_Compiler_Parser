import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HackCompiler
{
	static String className;
	
	public static void main(String[] args) throws FileNotFoundException
	{
		String codeFinal = "";
		String strfile;
		File file = new File(args[0]);
		
		String tokenTest = "START TOKEN TEST\n";
		
		BufferedReader in;
		strfile = file.toString();
		if(file.isDirectory())
		{
			String[] dirContent = file.list();
			for(String d : dirContent)
			{
				d = file.toString() + "\\" + d;
				if(d.substring(d.length() - 5, d.length()).equals(".jack"))
				{
					in = new BufferedReader(new FileReader(new File(d)));
					strfile = file.toString();
					className = d.substring(d.lastIndexOf("\\") + 1, d.length() - 5);
					/*if(codeFinal.isEmpty())
						//codeFinal = parser(tokenizer(in));
					else
						codeFinal = codeFinal.concat(parser(tokenizer(in)));*/
					
					//this is to test tokening
					Token[] tokArray = tokenizer(in);
					for(Token t : tokArray)
						codeFinal = codeFinal.concat(t.toString());
				}
			}
		}
		else
		{
			if(strfile.substring(strfile.length() - 5, strfile.length()).equals(".jack"))
			{
				in = new BufferedReader(new FileReader(file));
				strfile = file.toString();
				className = strfile.substring(strfile.lastIndexOf("\\") + 1, strfile.length() - 5);
				
				/*if(codeFinal.isEmpty())
					codeFinal = parser(tokenizer(in));
				else
					codeFinal = codeFinal.concat(parser(tokenizer(in)));*/
				
				//this is to test tokening
				Token[] tokArray = tokenizer(in);
				for(Token t : tokArray)
					codeFinal = codeFinal.concat(t.toString());
			}
		}
	}
	
	public static Token[] tokenizer(BufferedReader read) throws IOException
	{
		ArrayList<String> strArr = new ArrayList<String>();
		char[] chArr;
		String lineTemp = read.readLine();
		String wordTemp = "";
		boolean strLit = false;
		
		while(!lineTemp.equals(null))
		{
			chArr = lineTemp.toCharArray();
			for(char c : chArr)
			{
				if(Character.toString(c).equals("\""))
				{
					if(strLit == false)
						strLit = true;
					else
						strLit = false;
				}
				
				if(strLit == true && !Character.toString(c).equals("\""))
				{
					wordTemp = wordTemp.concat(Character.toString(c));
				}
				else if(strLit == false && !Character.toString(c).equals("\"") &&
						!Character.toString(c).isBlank())
				{
					wordTemp = wordTemp.concat(Character.toString(c));
				}
				
				//if c is blank and strlit is false or if c is " and strlit is false, add to string array
				if(strLit == false && (Character.toString(c).isBlank() || Character.toString(c).equals("\"")))
				{
					strArr.add(wordTemp);
					wordTemp = "";
				}
			}
			strArr.add(wordTemp);
		}
	}
}
