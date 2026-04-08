package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.TrainingPlanDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.TrainingPlan;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingPlanService {
    private final TrainingPlanDao trainingPlanDao;

    public TrainingPlanService(TrainingPlanDao trainingPlanDao) {
        this.trainingPlanDao = trainingPlanDao;
    }

    public List<TrainingPlan> getTrainingPlanList(ExerciseFilter filter) throws Exception {
        try {
            return trainingPlanDao.getTrainingPlanList(filter);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public TrainingPlan getTrainingPlanById(String id, ExerciseFilter filter) throws Exception {
        try {
            filter.setEntityId(id);
            List<TrainingPlan> trainingPlans = trainingPlanDao.getTrainingPlanList(filter);
            if (trainingPlans.isEmpty()) {
                throw new CustomException();
            }
            return trainingPlans.get(0);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public TrainingPlan createTrainingPlan(TrainingPlan trainingPlan) throws Exception {
        try {
            return trainingPlanDao.createTrainingPlan(trainingPlan);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateTrainingPlan(TrainingPlan trainingPlan) throws Exception {
        try {
            trainingPlanDao.updateTrainingPlan(trainingPlan);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteTrainingPlan(String id) throws Exception {
        try {
            trainingPlanDao.deleteTrainingPlan(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}
