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
		String string = "\t<" + this.type + ">" + this.name + "</" + this.type + ">\n";
		return string;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getName()
	{
		return name;
	}
}

