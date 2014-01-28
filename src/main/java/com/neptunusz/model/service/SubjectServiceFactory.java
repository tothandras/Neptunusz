package com.neptunusz.model.service;

/**
 * Created by Andrew on 1/28/14.
 * Singleton factory class for {@link com.neptunusz.model.service.SubjectService}
 */
public class SubjectServiceFactory {
    private static final SubjectService subjectService = new SubjectService();

    /**
     * Get the singleton instance of the {@link com.neptunusz.model.service.SubjectService}
     *
     * @return the {@link com.neptunusz.model.service.SubjectService}
     */
    public static SubjectService getInstance() {
        return subjectService;
    }
}
