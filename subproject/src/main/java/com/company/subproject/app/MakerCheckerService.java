package com.company.subproject.app;

import com.company.subproject.entity.ActionStatus;
import com.company.subproject.entity.ApprovalMessage;
import com.company.subproject.entity.ApproveStatus;
import com.company.subproject.entity.MakerChecker;
import io.jmix.core.EntitySerialization;
import io.jmix.core.Metadata;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Service
public class MakerCheckerService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MakerCheckerService.class);
    @Autowired
    private Metadata metadata;
//    @Inject
//    private Logger logger;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private EntitySerialization entitySerialization;

    @Transactional
    public void onUpdateMakerChecker(MakerChecker makerCheckerEntity, String updateJson) {
//        try (Transaction tx = persistence.createTransaction()) {
            JSONObject jsonObject = new JSONObject(entitySerialization.toJson(makerCheckerEntity));
//            EntityManager em = persistence.getEntityManager();
            MakerChecker checker = entityManager.find(metadata.getClass(jsonObject.getString("_entityName")).getJavaClass(), makerCheckerEntity.getId());
            checker.setUpdateData(updateJson);
            checker.setApprovalStatus(ApproveStatus.NOT_APPROVED);
            checker.setAction(ActionStatus.MODIFICATION);
            entityManager.merge(checker);
//            tx.commit();
//        }

    }

    @Transactional
    public void onApproveUpdate(MakerChecker makerCheckerEntity, Boolean approved, String message) {

        ApproveStatus approveStatus = approved ? ApproveStatus.APPROVED : ApproveStatus.DECLINED;
        boolean isCreation = makerCheckerEntity.getAction().equals(ActionStatus.CREATION);

        makerCheckerEntity.setApprovalStatus(approveStatus);
        makerCheckerEntity.setMessage(message);

        if (isCreation || !approved) {
//            try (Transaction tx = persistence.createTransaction()) {
                //save the message
                if (makerCheckerEntity.getUpdateData() != null && !makerCheckerEntity.getUpdateData().isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    String entityName = jsonObject.getString("_entityName");
                    saveApprovalMessage(message, entityName, approved, makerCheckerEntity.getId());
                }
                else {
                    saveApprovalMessage(message, makerCheckerEntity.getClass().getSimpleName(), approved, makerCheckerEntity.getId());
                }

//                EntityManager em = persistence.getEntityManager();
                entityManager.merge(makerCheckerEntity);

//                entityManager.persist(makerCheckerEntity);

        }
        else {
            JSONObject jsonObject = new JSONObject(makerCheckerEntity.getUpdateData());
            String entityName = jsonObject.getString("_entityName");
            //save the message
            saveApprovalMessage(message,entityName , approved, makerCheckerEntity.getId());


            MakerChecker intendedChange = entitySerialization.entityFromJson(makerCheckerEntity.getUpdateData(), metadata.getClass(entityName));
            intendedChange.setUpdateData(null);
            intendedChange.setVersion(makerCheckerEntity.getVersion());
            intendedChange.setMessage(message);
            intendedChange.setApprovalStatus(approveStatus);
            intendedChange.setAction(ActionStatus.MODIFICATION);
            entityManager.merge(intendedChange);
        }
       // try (Transaction tx = persistence.createTransaction()) {

            //entityManager.persist(intendedChange);

       // }
//        catch (Exception e) {
//            String action = approved ? "approving" : "declining";
//            log.error("Error while {} {}. Error message <<{}>>", action, makerCheckerEntity.getClass().getName(), e.getMessage());
//        }
    }



    @Transactional
    public void saveApprovalMessage(String message, String entityName, Boolean approval, UUID entityId) {


        ApprovalMessage approvalMessage = metadata.create(ApprovalMessage.class);
        approvalMessage.setMessage(message);
        approvalMessage.setEntityName(entityName);
        approvalMessage.setApproved(approval);
        approvalMessage.setEntityId(entityId);
        entityManager.persist(approvalMessage);
    }
    }