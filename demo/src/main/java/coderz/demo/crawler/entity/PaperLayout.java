package coderz.demo.crawler.entity;

/**
 * 报纸版面
 * @author 朱洪亮
 *
 */
public class PaperLayout {
	
	private String url;
	
	private String layoutName;
	
	private String date;
	
	private String layoutImg;
	
	private Article[] articles;

	public String getLayoutName() {
		return layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLayoutImg() {
		return layoutImg;
	}

	public void setLayoutImg(String layoutImg) {
		this.layoutImg = layoutImg;
	}

	public Article[] getArticles() {
		return articles;
	}

	public void setArticles(Article[] articles) {
		this.articles = articles;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
