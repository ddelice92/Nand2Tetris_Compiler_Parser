//***********************
//THIS LINE IS A GITHUB TEST

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
			if(strfile.substring(strfile.length() - 3, strfile.length()).equals(".vm"))
			{
				in = new BufferedReader(new FileReader(file));
				strfile = file.toString();
				className = strfile.substring(strfile.lastIndexOf("\\") + 1, strfile.length() - 3);
				
				/*if(codeFinal.isEmpty())
					codeFinal = parser(tokenizer(in));
				else
					codeFinal = codeFinal.concat(parser(tokenizer(in)));*/
			}
			
			//this is to test tokening
			Token[] tokArray = tokenizer(in);
			for(Token t : tokArray)
				codeFinal = codeFinal.concat(t.toString());
		}
	}
	
	public static Token[] tokenizer(BufferedReader read)
	{
		ArrayList<char> chArr = new ArrayList<char>();
		
	}
}
