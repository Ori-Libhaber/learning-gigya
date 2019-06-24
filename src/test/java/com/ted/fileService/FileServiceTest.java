package com.ted.fileService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Ted
 */
public class FileServiceTest {
    
    public FileServiceTest() {
        MockitoAnnotations.initMocks(this);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testFindFile_givenLogFileIsPresent_andItIsNightTime_thenLogFileShouldBeMovedToDestination_positive() {
        
        // GIVEN
        // instantiate and setup the state of the SUT and its mocked dependencies
        
        // WHEN
        // set mocked behaviour for collabrators
        
        // INVOKE
        // execute the actual test method
        
        // THEN
        // asset on execution result or/and states of mocked collaborators
        
    }
    
    @Test
    public void testFindFile_givenLogFileIsPresent_andItIsMorningTime_thenLogFileShouldNotBeMovedToDestination_positive() {
        
        // GIVEN
        // instantiate and setup the state of the SUT and its mocked dependencies
        
        // WHEN
        // set mocked behaviour for collabrators
        
        // INVOKE
        // execute the actual test method
        
        // THEN
        // asset on execution result or/and states of mocked collaborators
        
    }
    
    @Test
    public void testFindFile_givenLogFileIsPresent_andItIsEveningTime_andCredintialsNotValid_thenLogFileShouldNotBeMovedToDestination_andShouldReinitilize_negative() {
        
        // GIVEN
        // instantiate and setup the state of the SUT and its mocked dependencies
        
        // WHEN
        // set mocked behaviour for collabrators
        
        // INVOKE
        // execute the actual test method
        
        // THEN
        // asset on execution result or/and states of mocked collaborators
        
    }
    
}
