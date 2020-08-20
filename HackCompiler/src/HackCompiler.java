import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HackCompiler
{
	static String className;
	
	public static void main(String[] args) throws IOException
	{
		String codeFinal = "";
		String strfile;
		File file = new File(args[0]);
		
		String tokenTest = "START TOKEN TEST\n";
		
		System.out.println("pre buffer");
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
			System.out.println("not directory");
			if(strfile.substring(strfile.length() - 5, strfile.length()).equals(".jack"))
			{
				System.out.println("jack file found");
				in = new BufferedReader(new FileReader(file));
				System.out.println("buffered reader initialized");
				strfile = file.toString();
				System.out.println(strfile);
				className = strfile.substring(strfile.lastIndexOf("\\") + 1, strfile.length() - 5);
				
				/*if(codeFinal.isEmpty())
					codeFinal = parser(tokenizer(in));
				else
					codeFinal = codeFinal.concat(parser(tokenizer(in)));*/
				
				//this is to test tokening
				Token[] tokArray = tokenizer(in);
				System.out.println("token array made");
				for(Token t : tokArray)
					codeFinal = codeFinal.concat(t.toString());
			}
		}
		
		System.out.println(tokenTest);
		System.out.println(codeFinal);
	}
	
	public static Token[] tokenizer(BufferedReader read) throws IOException
	{
		System.out.println("tokenizer started");
		ArrayList<Token> tokArr = new ArrayList<Token>();
		Token tokArrFin[];
		char[] chArr;
		String lineTemp = read.readLine();
		String wordTemp = "";
		boolean strLit = false;
		Token tokTemp;
		
		System.out.println(lineTemp);
		while(lineTemp != null)
		{
			chArr = lineTemp.toCharArray();
			for(char c : chArr)
			{
				/*if(Character.toString(c).equals("\""))
				{
					if(strLit == false)
						strLit = true;
					else
						strLit = false;
				}*/
				
				//if(strLit == true && !Character.toString(c).equals("\""))
				if(Character.isLetterOrDigit(c) || Character.toString(c).equals("_") ||
						Character.toString(c).equals("\""))
				{
					wordTemp = wordTemp.concat(Character.toString(c));
				}
				/*else if(strLit == false && !Character.toString(c).equals("\"") &&
						!Character.toString(c).isBlank())
				{
					wordTemp = wordTemp.concat(Character.toString(c));
				}*/
				
				//if c is blank and strlit is false or if c is " and strlit is false, add to string array
				if(isSymbol(Character.toString(c)))
				{
					//put word into token array after test
					if(isKeyword(wordTemp))
						tokTemp = new Token(wordTemp, "keyword");
					else if(isIntConst(wordTemp))
						tokTemp = new Token(wordTemp, "integerConstant");
					else if(isStringConst(wordTemp))
						tokTemp = new Token(wordTemp, "stringConstant");
					else
						tokTemp = new Token(wordTemp, "identifier");
					
					tokArr.add(tokTemp);
					tokTemp = new Token(Character.toString(c), "symbol");
					tokArr.add(tokTemp);
					wordTemp = "";
				}
				else if(Character.toString(c).isBlank() && wordTemp.length() > 0)
				{
					if(isKeyword(wordTemp))
						tokTemp = new Token(wordTemp, "keyword");
					else if(isIntConst(wordTemp))
					{
						tokTemp = new Token(wordTemp, "integerConstant");
						System.out.println("THIS IS AN INT : " + wordTemp);
					}
					else if(isStringConst(wordTemp))
						tokTemp = new Token(wordTemp, "stringConstant");
					else if(isSymbol(wordTemp))
						tokTemp = new Token(wordTemp, "symbol");
					else
						tokTemp = new Token(wordTemp, "identifier");
					
					
					tokArr.add(tokTemp);
					wordTemp = "";
				}
			}
			//run this check one more time because the final character in chArr will
			//not be a blank or a close double quote
			/*if(isKeyword(wordTemp))
				tokTemp = new Token(wordTemp, "keyword");
			else if(isSymbol(wordTemp))
				tokTemp = new Token(wordTemp, "symbol");
			else if(isIntConst(wordTemp))
				tokTemp = new Token(wordTemp, "integerConstant");
			else
				tokTemp = new Token(wordTemp, "identifier");
			
			tokArr.add(tokTemp);*/
			lineTemp = read.readLine();
			System.out.println(lineTemp);
		}
		
		tokArr.trimToSize();
		tokArrFin = new Token[tokArr.size()];
		for(int i = 0; i < tokArr.size(); i++)
		{
			tokArrFin[i] = tokArr.get(i);
		}
		
		return tokArrFin;
	}
	
	public static boolean isKeyword(String string)
	{
		boolean test = false;
		String keywords[] = {"class", "constructor", "function", "method", "field", "static", "var",
				"int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if",
				"else", "while", "return"};
		
		for(String k : keywords)
		{
			if(string.equals(k))
				test = true;
		}
		
		return test;
	}
	
	public static boolean isSymbol(String string)
	{
		boolean test = false;
		String symbols[] = {"{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-", "*", "/", "&", "|",
				"<", ">", "=", "~"};
		
		for(String s : symbols)
		{
			if(string.equals(s))
				test = true;
		}
		
		return test;
	}
	
	public static boolean isIntConst(String string)
	{
		boolean test = true;
		char chArr[] = string.toCharArray();
		
		for(char c : chArr)
		{
			if(!Character.isDigit(c))
				test = false;
		}
		
		return test;
	}
	
	public static boolean isStringConst(String string)
	{
		boolean test = false;
		
		if(string.substring(0, 1).equals("\"") && string.substring(string.length() - 1, string.length()).equals("\""))
		{
			test = true;
		}
		
		return test;
	}
}
