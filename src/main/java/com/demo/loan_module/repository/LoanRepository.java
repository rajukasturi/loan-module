package com.demo.loan_module.repository;

import com.demo.loan_module.entity.LoanApplication;
import com.demo.loan_module.enums.LoanStatus;
import com.demo.loan_module.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LoanApplication entity.
 * Provides JPA-based CRUD operations and custom queries.
 */
@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, Long>, 
                                        JpaSpecificationExecutor<LoanApplication> {
    
    /**
     * Finds loan applications by PAN number.
     * @param panNumber The PAN number to search for
     * @return List of loan applications for the given PAN
     */
    List<LoanApplication> findByPanNumber(String panNumber);
    
    /**
     * Finds loan applications by status.
     * @param status The loan status to filter by
     * @return List of loan applications with the specified status
     */
    List<LoanApplication> findByStatus(LoanStatus status);
    
    /**
     * Finds loan applications by loan type.
     * @param loanType The loan type to filter by
     * @return List of loan applications of the specified type
     */
    List<LoanApplication> findByLoanType(LoanType loanType);
    
    /**
     * Finds loan applications by PAN number and status.
     * @param panNumber The PAN number
     * @param status The status to filter by
     * @return List of loan applications matching both criteria
     */
    List<LoanApplication> findByPanNumberAndStatus(String panNumber, LoanStatus status);
    
    /**
     * Finds the most recent loan application for a PAN number.
     * @param panNumber The PAN number
     * @return Optional containing the most recent application
     */
    @Query("SELECT la FROM LoanApplication la WHERE la.panNumber = ?1 " +
           "ORDER BY la.createdAt DESC LIMIT 1")
    Optional<LoanApplication> findMostRecentByPanNumber(String panNumber);
    
    /**
     * Counts loan applications by status.
     * @param status The status to count
     * @return Number of applications with the specified status
     */
    long countByStatus(LoanStatus status);
    
    /**
     * Counts loan applications by loan type.
     * @param loanType The loan type to count
     * @return Number of applications of the specified type
     */
    long countByLoanType(LoanType loanType);
    
    /**
     * Checks if a loan application exists by PAN number.
     * @param panNumber The PAN number to check
     * @return true if at least one application exists for the PAN
     */
    boolean existsByPanNumber(String panNumber);
}
