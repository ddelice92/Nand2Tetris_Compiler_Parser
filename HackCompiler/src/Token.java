public class Token
{
	String name, type;
	
	public Token(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	
	public String toString()
	{
		String string = "<" + this.type + ">" + this.name + "</" + this.type + ">\n";
		return string;
	}
}
