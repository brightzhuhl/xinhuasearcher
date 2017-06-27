package coderz.demo.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;

import coderz.demo.search.SearchUtil;

@WebServlet(name="searchServlet",urlPatterns="/search")
public class SearchServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 122543407582568053L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String keyword = req.getParameter("keyword");
		if(StringUtils.isEmpty(req.getParameter("p"))&&StringUtils.isNotEmpty(keyword)){
			listSearchResult(keyword,req,res);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if("listkeyword".equals(req.getParameter("p"))){
			getKeyword(res);
		}
	}
	
	public void getKeyword(HttpServletResponse res){
		res.setCharacterEncoding("utf-8");
		OutputStream out = null;
		try {
			out = res.getOutputStream();
			out.write(SearchUtil.getKeyword(10).toJSONString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listSearchResult(String keyword,HttpServletRequest req,HttpServletResponse res){
		JSONArray result = SearchUtil.searchKeyWord(keyword);
		try {
			req.setAttribute("result", result);
			req.getRequestDispatcher("/result.jsp").forward(req, res);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}
}
