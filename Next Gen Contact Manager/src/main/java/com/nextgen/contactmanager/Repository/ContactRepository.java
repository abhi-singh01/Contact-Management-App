package com.nextgen.contactmanager.Repository;
// JPA repository for contact with custom query methods.
import com.nextgen.contactmanager.Model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUserEmail(String userEmail);  // Find contacts by user email

    java.util.Optional<Contact> findByContactIdAndUserEmail(Long contactId, String userEmail);

    List<Contact> findByUserEmailAndFavoriteTrue(String userEmail);

    List<Contact> findByUserEmailAndCategoryIgnoreCase(String userEmail, String category);

    @Query("SELECT c FROM Contact c WHERE c.user.email = :userEmail AND " +
           "((:searchType = 'ALL' AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.emailId) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "OR (:searchType = 'NAME' AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "OR (:searchType = 'PHONE' AND LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "OR (:searchType = 'EMAIL' AND LOWER(c.emailId) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    List<Contact> advancedSearch(@Param("userEmail") String userEmail,
                                 @Param("keyword") String keyword,
                                 @Param("searchType") String searchType);
}
