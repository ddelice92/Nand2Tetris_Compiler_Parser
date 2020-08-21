import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HackCompiler
{
	static String className;
	
	public static void main(String[] args) throws IOException
	{
		String codeFinal = "<tokens>\n";
		String strfile;
		File file = new File(args[0]);
		
		String tokenTest = "START TOKEN TEST";
		
		BufferedReader in;
		BufferedWriter out;
		File fileOut;
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
					
					//this is to test tokening
					Token[] tokArray = tokenizer(in);
					for(Token t : tokArray)
						codeFinal = codeFinal.concat(t.toString());
					
					System.out.println("THIS IS D : " + d);
					strfile = strfile.substring(0, strfile.lastIndexOf("\\"));
					strfile = strfile.substring(0, strfile.lastIndexOf("\\"));
					System.out.println("THIS WILL BE FINAL FILE NAME : " + strfile.concat("\\My" + className
							+ "T.xml"));
					
					fileOut = new File(strfile.concat("\\My" + className + "T.xml"));
					//check if file already exists
					if(fileOut.exists())
					{
						fileOut.delete();
						fileOut.createNewFile();
					}
					else
						fileOut.createNewFile();
					out = new BufferedWriter(new FileWriter(fileOut));
					
					out.write(codeFinal);
					out.close();
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
				
				
				//this is to test tokening
				Token[] tokArray = tokenizer(in);
				for(Token t : tokArray)
				{
					codeFinal = codeFinal.concat(t.toString());
				}
				codeFinal = codeFinal.concat("</tokens>");
				
				strfile = strfile.substring(0, strfile.lastIndexOf("\\"));
				System.out.println("THIS WILL BE FINAL FILE NAME : " + strfile.concat("\\My" + className
						+ "T.xml"));
				
				fileOut = new File(strfile.concat("\\My" + className + "T.xml"));
				//check if file already exists
				if(fileOut.exists())
				{
					fileOut.delete();
					fileOut.createNewFile();
				}
				else
					fileOut.createNewFile();
				out = new BufferedWriter(new FileWriter(fileOut));
				
				out.write(codeFinal);
				out.close();
			}
		}
		
		System.out.println(tokenTest);
		System.out.println(codeFinal);
	}
	
	public static Token[] tokenizer(BufferedReader read) throws IOException
	{
		ArrayList<Token> tokArr = new ArrayList<Token>();
		Token tokArrFin[];
		char[] chArr;
		String lineTemp = read.readLine();
		String wordTemp = "";
		boolean inComment = false;
		Token tokTemp;
		
		while(lineTemp != null)
		{
			if(lineTemp.length() > 2)
			{
				if(!lineTemp.substring(0,2).equals("//"))
				{
					if(lineTemp.contains("//"))
						lineTemp = lineTemp.substring(0 , lineTemp.indexOf("/"));
					lineTemp = lineTemp.trim();
					
					chArr = lineTemp.toCharArray();
					for(int i = 0; i < chArr.length; i++)
					{
						System.out.println("INCOMMENT : " + inComment + " : WORDTEMP : " + wordTemp);
						
						if(Character.toString(chArr[i]).equals("/") && i < (chArr.length - 1) &&
								Character.toString(chArr[i + 1]).equals("*"))
						{
							if(!inComment && wordTemp.length() > 0)
							{
								if(isKeyword(wordTemp))
									tokTemp = new Token(wordTemp, "keyword");
								else if(isIntConst(wordTemp))
									tokTemp = new Token(wordTemp, "integerConstant");
								else if(isStringConst(wordTemp))
								{
									wordTemp = wordTemp.substring(1, wordTemp.length() - 1);
									tokTemp = new Token(wordTemp, "stringConstant");
								}
								else if(isSymbol(wordTemp))
								{
									if(wordTemp.equals("<"))
										tokTemp = new Token("&lt;", "symbol");
									else if(wordTemp.equals(">"))
										tokTemp = new Token("&gt;", "symbol");
									else if(wordTemp.equals("&"))
										tokTemp = new Token("&amp;", "symbol");
									else
										tokTemp = new Token(wordTemp, "symbol");
								}
								else
									tokTemp = new Token(wordTemp, "identifier");
								
								
								tokArr.add(tokTemp);
								wordTemp = "";
							}
							
							inComment = true;
							wordTemp = wordTemp.concat(Character.toString(chArr[i]));
						}
						else if(inComment)
							wordTemp = wordTemp.concat(Character.toString(chArr[i]));
						else if(wordTemp.length() > 0 && Character.toString(wordTemp.charAt(0)).equals("\"")
								&& !Character.toString(wordTemp.charAt(wordTemp.length() - 1)).equals("\""))
							wordTemp = wordTemp.concat(Character.toString(chArr[i]));
						else if(Character.isLetterOrDigit(chArr[i]) || Character.toString(chArr[i]).equals("_") ||
								Character.toString(chArr[i]).equals("\""))
						{
							wordTemp = wordTemp.concat(Character.toString(chArr[i]));
						}
						
						//if c is blank and strlit is false or if c is " and strlit is false, add to string array
						if(!inComment)
						{
							if(wordTemp.isBlank() || !Character.toString(wordTemp.charAt(0)).equals("\""))
							{
								if(isSymbol(Character.toString(chArr[i])))
								{
									//put word into token array after test
									if(wordTemp.length() > 0)
									{
										if(isKeyword(wordTemp))
											tokTemp = new Token(wordTemp, "keyword");
										else if(isIntConst(wordTemp))
											tokTemp = new Token(wordTemp, "integerConstant");
										else
											tokTemp = new Token(wordTemp, "identifier");
										
										tokArr.add(tokTemp);
									}
									
									if(Character.toString(chArr[i]).equals("<"))
										tokTemp = new Token("&lt;", "symbol");
									else if(Character.toString(chArr[i]).equals(">"))
										tokTemp = new Token("&gt;", "symbol");
									else if(Character.toString(chArr[i]).equals("&"))
										tokTemp = new Token("&amp;", "symbol");
									else
										tokTemp = new Token(Character.toString(chArr[i]), "symbol");
									tokArr.add(tokTemp);
									wordTemp = "";
								}
								else if(Character.toString(chArr[i]).isBlank() && wordTemp.length() > 0)
								{
									if(isKeyword(wordTemp))
										tokTemp = new Token(wordTemp, "keyword");
									else if(isIntConst(wordTemp))
										tokTemp = new Token(wordTemp, "integerConstant");
									else if(isStringConst(wordTemp))
									{
										wordTemp = wordTemp.substring(1, wordTemp.length() - 1);
										tokTemp = new Token(wordTemp, "stringConstant");
									}
									else if(isSymbol(wordTemp))
									{
										if(wordTemp.equals("<"))
											tokTemp = new Token("&lt;", "symbol");
										else if(wordTemp.equals(">"))
											tokTemp = new Token("&gt;", "symbol");
										else if(wordTemp.equals("&"))
											tokTemp = new Token("&amp;", "symbol");
										else
											tokTemp = new Token(wordTemp, "symbol");
									}
									else
										tokTemp = new Token(wordTemp, "identifier");
									
									
									tokArr.add(tokTemp);
									wordTemp = "";
								}
							}
							else if(Character.toString(wordTemp.charAt(wordTemp.length() - 1)).equals("\"") &&
									(Character.toString(chArr[i]).isBlank() || isSymbol(Character.toString(chArr[i]))))
							{
								wordTemp = wordTemp.substring(1, wordTemp.length() - 1);
								tokTemp = new Token(wordTemp, "stringConstant");
								tokArr.add(tokTemp);
								wordTemp = "";
								
								if(isSymbol(Character.toString(chArr[i])))
								{
									if(Character.toString(chArr[i]).equals("<"))
										tokTemp = new Token("&lt;", "symbol");
									else if(Character.toString(chArr[i]).equals(">"))
										tokTemp = new Token("&gt;", "symbol");
									else if(Character.toString(chArr[i]).equals("&"))
										tokTemp = new Token("&amp;", "symbol");
									else
										tokTemp = new Token(Character.toString(chArr[i]), "symbol");
									tokArr.add(tokTemp);
								}
							}
						}
						
						if(wordTemp.length() > 1 && wordTemp.substring(wordTemp.length() - 2).equals("*/"))
						{
							inComment = false;
							wordTemp = "";
						}
					}
				}
			}
			else
			{
				chArr = lineTemp.toCharArray();
				for(int i = 0; i < chArr.length; i++)
				{
					if(Character.toString(chArr[i]).equals("/") && i < (chArr.length - 1) &&
							Character.toString(chArr[i + 1]).equals("*"))
					{
						if(!inComment && wordTemp.length() > 0)
						{
							if(isKeyword(wordTemp))
								tokTemp = new Token(wordTemp, "keyword");
							else if(isIntConst(wordTemp))
								tokTemp = new Token(wordTemp, "integerConstant");
							else if(isStringConst(wordTemp))
							{
								wordTemp = wordTemp.substring(1, wordTemp.length() - 1);
								tokTemp = new Token(wordTemp, "stringConstant");
							}
							else if(isSymbol(wordTemp))
							{
								if(wordTemp.equals("<"))
									tokTemp = new Token("&lt;", "symbol");
								else if(wordTemp.equals(">"))
									tokTemp = new Token("&gt;", "symbol");
								else if(wordTemp.equals("&"))
									tokTemp = new Token("&amp;", "symbol");
								else
									tokTemp = new Token(wordTemp, "symbol");
							}
							else
								tokTemp = new Token(wordTemp, "identifier");
							
							
							tokArr.add(tokTemp);
							wordTemp = "";
						}
						
						inComment = true;
						wordTemp = wordTemp.concat(Character.toString(chArr[i]));
					}
					else if(Character.isLetterOrDigit(chArr[i]) || Character.toString(chArr[i]).equals("_") ||
							Character.toString(chArr[i]).equals("\""))
					{
						wordTemp = wordTemp.concat(Character.toString(chArr[i]));
					}
					
					//if c is blank and strlit is false or if c is " and strlit is false, add to string array
					if(isSymbol(Character.toString(chArr[i])))
					{
						//put word into token array after test
						if(wordTemp.length() > 0)
						{
							if(isKeyword(wordTemp))
								tokTemp = new Token(wordTemp, "keyword");
							else if(isIntConst(wordTemp))
								tokTemp = new Token(wordTemp, "integerConstant");
							else if(isStringConst(wordTemp))
							{
								wordTemp = wordTemp.substring(1, wordTemp.length() - 1);
								tokTemp = new Token(wordTemp, "stringConstant");
							}
							else
								tokTemp = new Token(wordTemp, "identifier");
							
							tokArr.add(tokTemp);
						}
						
						if(Character.toString(chArr[i]).equals("<"))
							tokTemp = new Token("&lt;", "symbol");
						else if(Character.toString(chArr[i]).equals(">"))
							tokTemp = new Token("&gt;", "symbol");
						else if(Character.toString(chArr[i]).equals("&"))
							tokTemp = new Token("&amp;", "symbol");
						else
							tokTemp = new Token(Character.toString(chArr[i]), "symbol");
						tokArr.add(tokTemp);
						wordTemp = "";
					}
					else if(Character.toString(chArr[i]).isBlank() && wordTemp.length() > 0)
					{
						if(isKeyword(wordTemp))
							tokTemp = new Token(wordTemp, "keyword");
						else if(isIntConst(wordTemp))
						{
							tokTemp = new Token(wordTemp, "integerConstant");
						}
						else if(isStringConst(wordTemp))
						{
							wordTemp = wordTemp.substring(1, wordTemp.length() - 1);
							tokTemp = new Token(wordTemp, "stringConstant");
						}
						else if(isSymbol(wordTemp))
						{
							if(wordTemp.equals("<"))
								tokTemp = new Token("&lt;", "symbol");
							else if(wordTemp.equals(">"))
								tokTemp = new Token("&gt;", "symbol");
							else if(wordTemp.equals("&"))
								tokTemp = new Token("&amp;", "symbol");
							else
								tokTemp = new Token(wordTemp, "symbol");
						}
						else
							tokTemp = new Token(wordTemp, "identifier");
						
						
						tokArr.add(tokTemp);
						wordTemp = "";
					}
				}
				
				if(wordTemp.length() > 1 && wordTemp.substring(wordTemp.length() - 2).equals("*/"))
				{
					inComment = false;
				}
			}
			
			lineTemp = read.readLine();
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
		
		try
		{
			Integer.parseInt(string);
		}
		catch(Exception e)
		{
			test = false;
		}
		
		return test;
	}
	
	public static boolean isStringConst(String string)
	{
		boolean test = false;
		if(string.length() > 1)
		{
			if(string.substring(0, 1).equals("\"") && string.substring(string.length() - 1, string.length()).equals("\""))
			{
				test = true;
			}
		}
		
		return test;
	}
}
