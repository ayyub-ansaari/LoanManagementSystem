package com.loanmanagement.dao.impl;

import com.loanmanagement.dao.LoanTypeDao;
import com.loanmanagement.exception.DataAccessException;
import com.loanmanagement.model.LoanType;
import com.loanmanagement.util.DBConnection;


import javax.swing.text.html.HTMLDocument;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanTypeDaoImpl implements LoanTypeDao {
    @Override
    public void addLoanType(LoanType loanType) {

        String sql = "insert into loan_type" + "(name ,description , interest_rate , min_amount , max_amount , max_tenture_months, status)"+"values (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,loanType.getName());
            ps.setString(2,loanType.getDescription());
            ps.setDouble(3,loanType.getInterestRate());
            ps.setDouble(4,loanType.getMinAmount());
            ps.setDouble(5,loanType.getMaxAmount());
            ps.setInt(6, loanType.getMaxTenureMonths());
            ps.setString(7 , loanType.getStatus().name());

            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()){
                    loanType.setLoanTypeId(keys.getInt(1));
                }
            }

        }catch (SQLException e) {
            throw new DataAccessException(
                    "Could not add loan type '" + loanType.getName() + "' — the name may already exist", e);
        }
    }

    @Override
    public LoanType getLoanTypeById(int loanTypeId) {
        String sql = "select * from loan_types where loan_type_id = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

            ps.setInt(1,loanTypeId);
            try(ResultSet result = ps.executeQuery()){
                return result.next() ? mapRow(result) : null;
            }
        } catch (SQLException  e) {
            throw new DataAccessException("getloanTypeId failed for : "+loanTypeId,e);
        }

    }

    @Override
    public void updateLoanType(LoanType loanType) {
        String sql = "update loan_types set name = ? , description = ? , interest_rate = ?"+"min_amount = ? , max_amount = ? , max_tenure_months = ? , status = ?"+"where loan_type_id = ?";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, loanType.getName());
            ps.setString(2, loanType.getDescription());
            ps.setDouble(3, loanType.getInterestRate());
            ps.setDouble(4, loanType.getMinAmount());
            ps.setDouble(5, loanType.getMaxAmount());
            ps.setInt(6, loanType.getMaxTenureMonths());
            ps.setString(7, loanType.getStatus().name());
            ps.setInt(8, loanType.getLoanTypeId());

            ps.executeUpdate();

        }catch (SQLException e){
            throw new DataAccessException("updateLoanType failed "+loanType.getLoanTypeId() , e);
        }
    }

    @Override
    public List<LoanType> getAllLoanTypes() {
        String sql = "select * from loan_types order by loan_type_id";
        List <LoanType> types = new ArrayList<>();
        try(Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)){
            ResultSet result = ps.executeQuery();
            while (result.next()){
                types.add(mapRow(result));
            }
            return types;
        }catch(SQLException e){
            throw new DataAccessException("getAllLoanTypes failed " , e);
        }
    }

    @Override
    public void deleteLoanType(int loanTypeId) {
        String sql = "Delete from loan_types where loan_types_id = ?";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1,loanTypeId);
            ps.executeUpdate();

        }catch(SQLException e){
            throw new DataAccessException("deleteLoanType is failed:"+loanTypeId , e);
        }
    }
    private LoanType mapRow(ResultSet result) throws SQLException{
        LoanType loanType = new LoanType();
        loanType.setLoanTypeId(result.getInt("loan_type_id"));
        loanType.setName(result.getString("name"));
        loanType.setDescription(result.getString("description"));
        loanType.setInterestRate(result.getDouble("interest_rate"));
        loanType.setMinAmount(result.getDouble("min_amount"));
        loanType.setMaxAmount(result.getDouble("max_amount"));
        loanType.setMaxTenureMonths(result.getInt("max_tenure_months"));
        loanType.setStatus(result.getString("status"));
        return loanType;

    }
}
