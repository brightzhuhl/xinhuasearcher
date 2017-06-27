package coderz.demo.crawler.entity;
/**
 * 文章
 * @author 朱洪亮
 *
 */
public class Article {
	
	private String id;
	
	private String layoutUrl;

	private String date;

	private ArticleSummary summary;

	private String content;

	private String author;

	private String from;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArticleSummary getSummary() {
		return summary;
	}

	public void setSummary(ArticleSummary summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getLayoutUrl() {
		return layoutUrl;
	}

	public void setLayoutUrl(String layoutUrl) {
		this.layoutUrl = layoutUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
