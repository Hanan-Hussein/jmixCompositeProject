package com.company.subproject.screen.userviewchanges;

import com.company.subproject.app.MakerCheckerService;
import com.company.subproject.entity.MakerChecker;
import com.company.subproject.entity.User;
import io.jmix.audit.snapshot.EntitySnapshotManager;
import io.jmix.audit.snapshot.model.EntitySnapshotModel;
import io.jmix.core.*;

import java.io.Serializable;

import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.*;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Supplier;

@UiController("UserViewchangesScreen")
@UiDescriptor("user-viewchanges-screen.xml")
public class UserViewchangesScreen extends Screen implements ScreenOptions {

    @Autowired
    private EntitySerialization entitySerialization;
    @Autowired
    private EntitySnapshotManager entitySnapshotManager;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private TextField<String> activeField;
    @Autowired
    private Label<String> emailAddressId;
    @Autowired
    private TextField<String> emailAddressField;
    @Autowired
    private TextField<String> emailAddress2Field;
    @Autowired
    private Label<String> currentvalue;
    @Autowired
    private MakerCheckerService makerCheckerService;
    @Autowired
    private Label<String> activeId;
    @Autowired
    private Notifications notifications;
    @Autowired
    private TextField<String> active2Field;
    private UUID entityId;
    private MakerChecker entity;
    @Autowired
    private Label<String> userNameId;
    @Autowired
    private TextField<String> userNameField;
    @Autowired
    private TextField<String> userName2Field;
    @Autowired
    private Metadata metadata;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        UUID id = getEntityId();

        EntitySnapshotModel lastEntitySnapshot = entitySnapshotManager.getLastEntitySnapshot(metadata.getClass(User.class), id);
        if (lastEntitySnapshot != null) {
            User loadedAgent = dataManager.load(User.class).id(id).one();
            User agentSnapshot = entitySerialization.entityFromJson(lastEntitySnapshot.getSnapshotXml(), metadata.getClass(User.class));

            if (!agentSnapshot.getUsername().equals(loadedAgent.getUsername())) {
                userName2Field.setValue(loadedAgent.getUsername());
                userName2Field.setVisible(true);
                userNameField.setValue(agentSnapshot.getUsername());
                userNameField.setVisible(true);
            } else {
                userNameId.setVisible(false);
            }
            if (!agentSnapshot.getActive().equals(loadedAgent.getActive())) {
                activeField.setValue(loadedAgent.getActive().toString());
                activeField.setVisible(true);
                active2Field.setValue(agentSnapshot.getActive().toString());
                active2Field.setVisible(true);
            }else {
                activeId.setVisible(false);
            }

            if (!agentSnapshot.getEmail().equals(loadedAgent.getEmail())) {
                emailAddressField.setValue(loadedAgent.getEmail());
                emailAddressField.setVisible(true);
                emailAddress2Field.setValue(agentSnapshot.getEmail());
                emailAddress2Field.setVisible(true);
            }
        }
    }
    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public MakerChecker getEntity() {
        return entity;
    }

    public void setEntity(MakerChecker entity) {
        this.entity = entity;
    }
    @Subscribe("approveBtn")
    public void onApproveBtnClick(Button.ClickEvent event) {
        approveEntity(getEntity());
    }

    @Subscribe("declineBtn")
    public void onDeclineBtnClick(Button.ClickEvent event) {
        declineEntity(getEntity());
    }

    private void declineDialog() {
        notifications.create(Notifications.NotificationType.WARNING)
                .withCaption("Approval")
                .withContentMode(ContentMode.TEXT)
                .withHideDelayMs(3)
                .withDescription("Changes Declined!")
                .withCloseListener(closeEvent -> {
                    close(StandardOutcome.DISCARD);
                })
                .show();
    }

    private void approveDialog() {
        notifications.create(Notifications.NotificationType.HUMANIZED)
                .withCaption("Approval")
                .withContentMode(ContentMode.TEXT)
                .withDescription("Changes Approved successfully!")
                .withHideDelayMs(3000)
                .withCloseListener(close -> {
                    close(StandardOutcome.CLOSE);
                })
                .show();
    }

    private void approveEntity(MakerChecker entity) {
        dialogs.createOptionDialog()
                .withMessage("Are sure you want to approve this record?")
                .withCaption("Record approval confirmation!")
                .withActions(
                        new DialogAction(DialogAction.Type.YES).withHandler(e -> {

                            //launch an input dialog for getting the message
                            dialogs.createInputDialog(this)
                                    .withCaption("Enter the approval message")
                                    .withParameters(
                                            InputParameter.stringParameter("message").withField(getMessageField())
                                                    .withCaption("Message").withRequired(true)

                                    )
                                    .withActions(DialogActions.OK_CANCEL)
                                    .withCloseListener(closeEvent -> {
                                        if (closeEvent.closedWith(DialogOutcome.OK)) {
                                            String message = closeEvent.getValue("message");

                                            if (entity == null) return;
                                            makerCheckerService.onApproveUpdate(entity, true, message);
                                            approveDialog();
                                        }
                                    })
                                    .show();

                        }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();
    }

    public void declineEntity(MakerChecker entity) {
        dialogs.createOptionDialog()
                .withMessage("Are sure you want to decline this record?")
                .withCaption("Record approval confirmation!")
                .withActions(
                        new DialogAction(DialogAction.Type.YES).withHandler(e -> {


                            //launch an input dialog for getting the message
                            dialogs.createInputDialog(this)
                                    .withCaption("Enter the decline reason")
                                    .withParameters(
                                            InputParameter.stringParameter("message").withField(getMessageField())
                                                    .withCaption("Decline Message").withRequired(true)
                                    )
                                    .withActions(DialogActions.OK_CANCEL)
                                    .withCloseListener(closeEvent -> {
                                        if (closeEvent.closedWith(DialogOutcome.OK)) {
                                            String message = closeEvent.getValue("message");

                                            if (entity == null) return;
                                            makerCheckerService.onApproveUpdate(entity, false, message);
                                            declineDialog();
                                        }
                                    })
                                    .show();
                        }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .show();
    }

    private Supplier<Field> getMessageField() {
        return () -> {
            TextArea webTextArea = uiComponents.create(TextArea.class);
            webTextArea.setWidthFull();
            return webTextArea;
        };
    }
}