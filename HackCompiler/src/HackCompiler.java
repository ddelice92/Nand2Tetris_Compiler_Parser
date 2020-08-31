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
	static int index = 0;
	static ArrayList<String> subArr = new ArrayList<String>(), classVarsArr = new ArrayList<String>(),
			classNameArr = new ArrayList<String>(), paramList = new ArrayList<String>(),
			methVarsArr = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException
	{
		String codeFinal = "";
		String strfile;
		File file = new File(args[0]);
		
		BufferedReader in;
		BufferedWriter out;
		File fileOut;
		strfile = file.toString();
		if(file.isDirectory())
		{
			String[] dirContent = file.list();
			classNameArr.add("Math");
			classNameArr.add("String");
			classNameArr.add("Array");
			classNameArr.add("Output");
			classNameArr.add("Screen");
			classNameArr.add("Keyboard");
			classNameArr.add("Memory");
			classNameArr.add("Sys");
			for(String d : dirContent)
			{
				if(d.substring(d.length() - 5, d.length()).equals(".jack") &&
						!classNameArr.contains(d.substring(d.lastIndexOf("\\") + 1, d.length() - 5)))
					classNameArr.add(d.substring(d.lastIndexOf("\\") + 1, d.length() - 5));
			}
			for(String d : dirContent)
			{
				d = file.toString() + "\\" + d;
				if(d.substring(d.length() - 5, d.length()).equals(".jack"))
				{
					in = new BufferedReader(new FileReader(new File(d)));
					strfile = file.toString();
					className = d.substring(d.lastIndexOf("\\") + 1, d.length() - 5);
					System.out.println("************FILE :: " + className + " :: START PARSING***********\n");
					
					codeFinal = "";
					index = 0;
					subArr.clear();
					classVarsArr.clear();
					codeFinal = codeFinal.concat(parser(tokenizer(in)));
					
					fileOut = new File(strfile.concat("\\My" + className + ".xml"));
					//check if file already exists
					if(fileOut.exists())
					{
						fileOut.delete();
						fileOut.createNewFile();
					}
					else
						fileOut.createNewFile();
					out = new BufferedWriter(new FileWriter(fileOut));
					
					System.out.println(codeFinal + "************FINAL OUTPUT***********\n\n\n\n");
					out.write(codeFinal);
					out.flush();
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
				/*Token[] tokArray = tokenizer(in);
				for(Token t : tokArray)
					codeFinal = codeFinal.concat(t.toString());*/
				codeFinal = "";
				codeFinal = codeFinal.concat(parser(tokenizer(in)));
				
				fileOut = new File(strfile.concat("\\My" + className + ".xml"));
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
						lineTemp = lineTemp.substring(0 , lineTemp.indexOf("//"));
					lineTemp = lineTemp.trim();
					
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
	
	public static String parser(Token[] token)
	{
		int count;
		String codeFinal = "";
		
		//create arraylist of subroutine names and variable names
		for(int i = 0; i < token.length; i++)
		{
			String tempTok = token[i].getName();
			if((tempTok.equals("constructor") || tempTok.equals("function") || tempTok.equals("method") ||
					tempTok.equals("void") || tempTok.equals("int") || tempTok.equals("char") ||
					tempTok.equals("boolean") || tempTok.equals(className)) && token[i + 2].getName().equals("("))
			{
				subArr.add(token[i + 1].getName());
			}
			else if(token[i].getName().equals("static") || token[i].getName().equals("field"))
			{
				classVarsArr.add(token[i + 2].getName());
				if(!token[i + 3].getName().equals(";"))
				{
					count = 4;
					while(!token[i + count - 1].getName().equals(";"))
					{
						classVarsArr.add(token[i + count].getName());
						count = count + 2;
					}
				}
			}
		}
		
		codeFinal = codeFinal.concat(classMethod(token));
		return codeFinal;
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
	
	public static String classMethod(Token[] token)
	{
		String retString = "<class>\n";
		
		//parse class and class name
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		if(token[index].getName().equals("{"))
		{
			retString = retString.concat(token[index].toString());
			index++;
			while(!token[index].getName().equals("}"))
			{
				if(token[index].getName().equals("static") || token[index].getName().equals("field"))
					retString = retString.concat(classVarDec(token));
				else if(token[index].getName().equals("constructor") || token[index].getName().equals("function")
						|| token[index].getName().equals("method")/* || token[index].getName().equals("void") ||
						token[index].getName().equals("int") || token[index].getName().equals("char") ||
						token[index].getName().equals("boolean") || token[index].getName().equals(className)*/)
				{
					retString = retString.concat(subroutineDec(token));
				}
			}
			
			//print final close bracket
			retString = retString.concat(token[index].toString());
		}
		
		retString = retString.concat("</class>\n");
		return retString;
	}
	
	public static String classVarDec(Token[] token)
	{
		String retString = "<classVarDec>\n";
		
		//add static or field keyword, type, and first varName
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		
		//print list of varNames
		if(token[index].getName().equals(","))
		{
			while(!token[index].getName().equals(";"))
			{
				retString = retString.concat(token[index].toString());
				index++;
			}
		}
		
		//add semicolon
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat("</classVarDec>\n");
		return retString;
	}
	
	public static String subroutineDec(Token[] token)
	{
		String retString = "<subroutineDec>\n";
		
		//add keyword, type, subroutineName, and (
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		
		//check for parameters then add )
		retString = retString.concat(parameterList(token));
		retString = retString.concat(token[index].toString());
		index++;
		
		//add subroutine body
		retString = retString.concat(subroutineBody(token));
		paramList.clear();
		
		retString = retString.concat("</subroutineDec>\n");
		return retString;
	}
	
	public static String parameterList(Token[] token)
	{
		String retString = "<parameterList>\n";
		
		while(!token[index].getName().equals(")"))
		{
			//add type and varName
			retString = retString.concat(token[index].toString());
			index++;
			paramList.add(token[index].getName());
			retString = retString.concat(token[index].toString());
			index++;
			
			//add comma for multiple vars
			if(token[index].getName().equals(","))
			{
				retString = retString.concat(token[index].toString());
				index++;
			}
		}
		
		retString = retString.concat("</parameterList>\n");
		return retString;
	}
	
	public static String subroutineBody(Token[] token)
	{
		String retString = "<subroutineBody>\n";
		
		retString = retString.concat(token[index].toString());
		index++;
		
		while(!token[index].getName().equals("}"))
		{
			if(token[index].getName().equals("var"))
			{
				retString = retString.concat(varDec(token));
			}
			else
			{
				retString = retString.concat(statements(token));
			}
		}
		
		//add }
		retString = retString.concat(token[index].toString());
		index++;
		methVarsArr.clear();
		retString = retString.concat("</subroutineBody>\n");
		return retString;
	}
	
	public static String varDec(Token[] token)
	{
		String retString = "<varDec>\n";
		
		//add var, type, and varName
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		methVarsArr.add(token[index].getName());
		retString = retString.concat(token[index].toString());
		index++;
		
		if(token[index].getName().equals(","))
		{
			while(!token[index].getName().equals(";"))
			{
				//add comma and next varName
				retString = retString.concat(token[index].toString());
				index++;
				methVarsArr.add(token[index].getName());
				retString = retString.concat(token[index].toString());
				index++;
			}
		}
		
		//add ;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat("</varDec>\n");
		
		return retString;
	}
	
	public static String statements(Token[] token)
	{
		String retString = "<statements>\n";
		
		while(!token[index].getName().equals("}"))
		{
			//System.out.println("THIS IS THE CURRENT TOKEN :: " + token[index].getName());
			switch(token[index].getName())
			{
				case "let":
					retString = retString.concat(letMethod(token));
					break;
				case "if":
					retString = retString.concat(ifMethod(token));
					break;
				case "while":
					retString = retString.concat(whileMethod(token));
					break;
				case "do":
					retString = retString.concat(doMethod(token));
					break;
				case "return":
					retString = retString.concat(returnMethod(token));
					break;
			}
		}
		
		retString = retString.concat("</statements>\n");
		return retString;
	}
	
	public static String letMethod(Token[] token)
	{
		String retString = "<letStatement>\n";
		
		//add let and varName
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		
		//check for expression in brackets
		if(token[index].getName().equals("["))
		{
			//add [ expression ]
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(expression(token));
			retString = retString.concat(token[index].toString());
			index++;
		}
		
		//add =, expression, and ;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(expression(token));
		retString = retString.concat(token[index].toString());
		index++;
		
		retString = retString.concat("</letStatement>\n");
		//if(className.equals("Square"))
			//System.out.println(retString);
		return retString;
	}
	
	public static String ifMethod(Token[] token)
	{
		String retString = "<ifStatement>\n";
		
		//add if, (, expression, ), and {
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(expression(token));
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		
		//add statements and }
		retString = retString.concat(statements(token));
		retString = retString.concat(token[index].toString());
		index++;
		
		if(token[index].getName().equals("else"))
		{
			//add else, {, statements, }
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(statements(token));
			retString = retString.concat(token[index].toString());
			index++;
		}
		
		retString = retString.concat("</ifStatement>\n");
		return retString;
	}
	
	public static String whileMethod(Token[] token)
	{
		String retString = "<whileStatement>\n";
		
		//add while, (, expression, and )
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(expression(token));
		retString = retString.concat(token[index].toString());
		index++;
		
		//add {, statements, and }
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(statements(token));
		retString = retString.concat(token[index].toString());
		index++;
		
		retString = retString.concat("</whileStatement>\n");
		return retString;
	}
	
	public static String doMethod(Token[] token)
	{
		String retString = "<doStatement>\n";
		
		//add do and subroutineCall and ;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat(subroutineCall(token));
		retString = retString.concat(token[index].toString());
		index++;
		
		retString = retString.concat("</doStatement>\n");
		//if(className.equals("Square"))
			//System.out.println(retString);
		return retString;
	}
	
	public static String returnMethod(Token[] token)
	{
		String retString = "<returnStatement>\n";
		
		retString = retString.concat(token[index].toString());
		index++;
		if(!token[index].getName().equals(";"))
			retString = retString.concat(expression(token));
		
		//add ;
		retString = retString.concat(token[index].toString());
		index++;
		retString = retString.concat("</returnStatement>\n");
		return retString;
	}
	
	public static String expression(Token[] token)
	{
		String retString = "<expression>\n";
		
		retString = retString.concat(term(token));

		if(token[index].getName().equals("+") || token[index].getName().equals("-") ||
				token[index].getName().equals("*") || token[index].getName().equals("/") ||
				token[index].getName().equals("&amp;") || token[index].getName().equals("|") ||
				token[index].getName().equals("&lt;") || token[index].getName().equals("&gt;") ||
				token[index].getName().equals("="))
		{
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(term(token));
		}
		
		retString = retString.concat("</expression>\n");
		return retString;
	}
	
	public static String subroutineCall(Token[] token)
	{
		String retString = "";
		
		/*retString = retString.concat(token[index].toString());
		index++;*/
		if(!classNameArr.contains(token[index].getName()) && !classVarsArr.contains(token[index].getName())
				&& !methVarsArr.contains(token[index].getName()))
		{
			//if subroutine is static
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(expressionList(token));
			if(token[index].getName().equals(","))
			{
				while(!token[index].getName().equals(")"))
				{
					retString = retString.concat(token[index].toString());
					index++;
					retString = retString.concat(expression(token));
				}
			}
			retString = retString.concat(token[index].toString());
			index++;
		}
		else
		{
			//add className or varName, symbol(.), subroutineName, and (
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(expressionList(token));
			if(token[index].getName().equals(","))
			{
				while(!token[index].getName().equals(")"))
				{
					retString = retString.concat(token[index].toString());
					index++;
					retString = retString.concat(expressionList(token));
				}
			}
			retString = retString.concat(token[index].toString());
			index++;
		}
		
		//retString = retString.concat("");
		return retString;
	}
	
	public static String expressionList(Token[] token)
	{
		String retString = "<expressionList>\n";
		
		/*if(index + 1 < token.length && token[index + 1].getName().equals(","))
		{
			while(!token[index].getName().equals(")"))
			{
				retString = retString.concat(expression(token));
				if(token[index].getName().equals(","))
				{
					retString = retString.concat(token[index].toString());
					index++;
				}
			}
		}
		else if(!token[index].getName().equals(")"))
		{
			retString = retString.concat(expression(token));
		}*/
		
		while(!token[index].getName().equals(")"))
		{
			retString = retString.concat(expression(token));
			if(token[index].getName().equals(","))
			{
				retString = retString.concat(token[index].toString());
				index++;
			}
		}
		
		retString = retString.concat("</expressionList>\n");
		//if(className.equals("Square"))
			//System.out.println(retString + "\n*************************");
		return retString;
	}
	
	public static String term(Token[] token)
	{
		String retString = "";
		
		if(token[index].getType().equals("integerConstant"))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getType().equals("stringConstant"))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("true"))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("false"))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("null"))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("this"))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("-"))
		{
			//add unary operator and term
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(term(token));
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("~"))
		{
			//add unary operator and term
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(term(token));
			retString = retString.concat("</term>\n");
		}
		else if(classVarsArr.contains(token[index].getName()) || methVarsArr.contains(token[index].getName()))
		{
			//add class variable name or method variable name, and expression in bracket
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			
			if(token[index].getName().equals("["))
			{
				retString = retString.concat(token[index].toString());
				index++;
				retString = retString.concat(expression(token));
				retString = retString.concat(token[index].toString());
				index++;
			}
			retString = retString.concat("</term>\n");
		}
		else if(subArr.contains(token[index].toString()) || classNameArr.contains(token[index].getName()))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(subroutineCall(token));
			retString = retString.concat("</term>\n");
		}
		else if(paramList.contains(token[index].getName()))
		{
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		else if(token[index].getName().equals("("))
		{
			//add (, expression, )
			retString = retString.concat("<term>\n");
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat(expression(token));
			retString = retString.concat(token[index].toString());
			index++;
			retString = retString.concat("</term>\n");
		}
		
		return retString;
	}
}
