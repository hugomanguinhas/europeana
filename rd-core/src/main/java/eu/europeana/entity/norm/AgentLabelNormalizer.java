package eu.europeana.entity.norm;

public class AgentLabelNormalizer implements LiteralNormalizer
{

	@Override
	public String normalize(String label)
	{
		return (label == null ? null : label.substring(0, label.indexOf('(')).trim());
	}
}
