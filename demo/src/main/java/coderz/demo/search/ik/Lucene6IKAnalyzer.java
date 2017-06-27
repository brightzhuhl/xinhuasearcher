package coderz.demo.search.ik;

import org.apache.lucene.analysis.Analyzer;

public class Lucene6IKAnalyzer extends Analyzer{

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Lucene6IKTokenizer tokenizer = new Lucene6IKTokenizer( true);
		TokenStreamComponents components = new TokenStreamComponents(tokenizer);
		return components;
	}
}
