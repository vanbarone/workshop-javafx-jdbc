package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO seller " +
			                           "(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
					                   "VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffect  = st.executeUpdate();
			
			if (rowsAffect > 0) {
				ResultSet rs = st.getGeneratedKeys();
				
				if (rs.next()) {
					obj.setId(rs.getInt(1));
				}
				
				DB.closeResultset(rs);
			}
			else {
				throw new DbException("Nenhum registro incluído");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		
		try {
			conn = DB.getConnection();
			
			st = conn.prepareStatement("UPDATE seller "
					                 + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
			                  		 + "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			conn = DB.getConnection();
			
			st = conn.prepareStatement("DELETE FROM seller\r\n"
				                     + "WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rowsAffect = st.executeUpdate();
			
			if (rowsAffect == 0) {
				throw new DbException("Deleção não executada - Id não encontrado");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName " 
					                 + "FROM seller INNER JOIN department "
					                 + "ON seller.DepartmentId = department.Id "
					                 + "WHERE seller.Id = ?");
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if (rs.next()) {			
				Department dep = instantiateDepartment(rs);
				
				Seller seller = instantiateSeller(rs,dep);
				
				return seller;
			} 
			else {
				return null;
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultset(rs);
		}
	}
	
	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					                 + "FROM seller INNER JOIN department " 
					                 + "ON seller.DepartmentId = department.Id " 
					                 + "ORDER BY Name");
			
			rs = st.executeQuery();
			
			Map<Integer, Department> map = new HashMap<>();
			List<Seller> list = new ArrayList<>();
			
			while (rs.next()) {	
				Department dep = map.get(rs.getInt("DepartmentId")); //map.get retorna nulo se não encontrar
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
					
				Seller seller = instantiateSeller(rs,dep);
				
				list.add(seller);
			} 
			
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultset(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					                 + "FROM seller INNER JOIN department " 
					                 + "ON seller.DepartmentId = department.Id " 
					                 + "WHERE DepartmentId = ? "
					                 + "ORDER BY Name");
			
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			Map<Integer, Department> map = new HashMap<>();
			List<Seller> list = new ArrayList<>();
			
			while (rs.next()) {	
				Department dep = map.get(rs.getInt("DepartmentId")); //map.get retorna nulo se não encontrar
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
					
				Seller seller = instantiateSeller(rs,dep);
				
				list.add(seller);
			} 
			
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultset(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		
		Seller seller = new Seller();
		
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));		
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(dep);
		
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		
		Department dep = new Department();
		
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		
		return dep;
	}
	
}
