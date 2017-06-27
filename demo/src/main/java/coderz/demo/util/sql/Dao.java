package coderz.demo.util.sql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import coderz.demo.crawler.entity.Article;
import coderz.demo.pool.sql.SqlConnectionPool;
import coderz.demo.util.sql.annotation.Column;


public class Dao {
	private static Log logger = LogFactory.getLog(Dao.class);
	public static List<String[]> query(int columnCount,String sql,Object... args){
		Connection conn = null;
		try {
			conn = SqlConnectionPool.getInstance().borrowObject();
			PreparedStatement pst = conn.prepareStatement(sql);
			for(int i=0;i<args.length;i++){
				pst.setObject(i+1, args[i]);
			}
			ResultSet resSet = pst.executeQuery();
			List<String[]> result = new ArrayList<>();
			while(resSet.next()){
				String[] row = new String[columnCount];
				for(int i=0;i<columnCount;i++){
					row[i] = resSet.getString(i+1);
				}
				result.add(row);
			}
			return result ;
		} catch (SQLException e) {
			logger.error("sql查询出错：",e);
		} catch (Exception e) {
			logger.error("sql查询出错：",e);
		}finally {
			if(conn != null){
				SqlConnectionPool.getInstance().returnObject(conn);
			}
		}
		return new ArrayList<>();
	}
	
	
	public static boolean execute(String sql,Object... args){
		Connection conn = null;
		try {
			conn = SqlConnectionPool.getInstance().borrowObject();
			PreparedStatement pst = conn.prepareStatement(sql);
			for(int i=0;i<args.length;i++){
				pst.setObject(i+1, args[i]);
			}
			pst.execute();
			return true;
		} catch (SQLException e) {
			logger.error("sql操作出错：",e);
		} catch (Exception e) {
			logger.error("sql操作出错：",e);
		}finally {
			if(conn != null){
				SqlConnectionPool.getInstance().returnObject(conn);
			}
		}
		return false;
	}
	
	public static <T> List<T> queryAndWrapResultWithEntity(int columnCount,String sql,Class<T> clazz,Object... args){
			//List<String[]> queryRes = query(columnCount, sql, args);
		Connection conn = null;
		try {
			conn = SqlConnectionPool.getInstance().borrowObject();
			PreparedStatement pst = conn.prepareStatement(sql);
			for(int i=0;i<args.length;i++){
				pst.setObject(i+1, args[i]);
			}
			ResultSet resSet = pst.executeQuery();
			
			
			Field[] fields = clazz.getDeclaredFields();
			Map<String,Field> fieldMap = new HashMap<>();
			for(Field f:fields){
				Column annotation = f.getAnnotation(Column.class);
				if(annotation != null){
					String column = annotation.targetName();
					fieldMap.put(column, f);
				}else{
					fieldMap.put(f.getName(), f);
				}
			}
			
			List<T> resultLi = new ArrayList<>();
			while(resSet.next()){
				try {
					T ins = clazz.newInstance();
					for(int i=0; i<columnCount; i++){
						Field f = fieldMap.get(resSet.getMetaData().getColumnName(i));
						if(f != null)
							f.set(ins, resSet.getObject(i));
					}
					resultLi.add(ins);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			return resultLi;
		} catch (SQLException e) {
			logger.error("sql查询出错：",e);
		} catch (Exception e) {
			logger.error("sql查询出错：",e);
		}finally {
			if(conn != null){
				SqlConnectionPool.getInstance().returnObject(conn);
			}
		}
		return new ArrayList<>();
	}
	
	public static void main(String[] args) {
		queryAndWrapResultWithEntity(0,"select password,username,hahah from talbe",Article.class);
	}
}
