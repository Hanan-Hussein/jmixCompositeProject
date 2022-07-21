package com.company.subproject.screen.user;

import com.company.subproject.app.MakerCheckerService;
import com.company.subproject.entity.ApproveStatus;
import com.company.subproject.entity.User;
import com.company.subproject.screen.userviewchanges.UserViewchangesScreen;
import io.jmix.audit.snapshot.EntitySnapshotManager;
import io.jmix.audit.snapshot.model.EntitySnapshotModel;
import io.jmix.core.AccessManager;
import io.jmix.core.Metadata;
import io.jmix.core.Resources;
import io.jmix.core.accesscontext.SpecificOperationAccessContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.ui.Dialogs;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.action.list.CreateAction;

import io.jmix.ui.action.list.RemoveAction;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.Field;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private CollectionContainer<User> usersDc;
    @Inject
    private Metadata metadata;
    @Named("usersTable.create")
    private CreateAction<User> userTableCreate;
    @Autowired
    private Dialogs dialogs;
    @Inject
    private GroupTable<User> usersTable;
    @Inject
    private CollectionLoader<User> usersDl;

    @Autowired
    private UiComponents uiComponents;
    @Named("usersTable.approve")
    private BaseAction userTableApprove;
    @Named("usersTable.decline")
    private BaseAction userTableDecline;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Named("usersTable.remove")
    private RemoveAction<User> userTableRemove;
    @Inject
    private MakerCheckerService makerCheckerService;
    @Autowired
    private Resources resources;

    @Autowired
    private EntitySnapshotManager entitySnapshotManager;
    @Autowired
    private AccessManager accessManager;
    @Named("usersTable.showSnapshots")
    private BaseAction userTableShowSnapshots;

    public UserBrowse() {
    }
//


    @Subscribe
    public void onInit(InitEvent event) {

        SpecificOperationAccessContext accessContext = new SpecificOperationAccessContext("maker");
        accessManager.applyRegisteredConstraints(accessContext);

//
       userTableCreate.setEnabled(accessContext.isPermitted());
        userTableRemove.setEnabled(accessContext.isPermitted());
//        userTableCreate.setEnabled(accessContext);
//        userTableRemove.setEnabled(accessContext);

        String login = currentAuthentication.getUser().getUsername();

        usersDc.addItemChangeListener(e -> {
            if (usersDc.getItemOrNull() == null) return;
            User selectedEntity = usersDc.getItem();
            ApproveStatus approvalStatus = selectedEntity.getApprovalStatus();
            boolean isTheMaker = login.equals(selectedEntity.getCreatedBy());
            boolean showApproveMenu = approvalStatus.equals(ApproveStatus.NOT_APPROVED);
            SpecificOperationAccessContext checkerContext = new SpecificOperationAccessContext("checker");
            accessManager.applyRegisteredConstraints(checkerContext);
            boolean canShowMenu = checkerContext.isPermitted()&& showApproveMenu && !isTheMaker;

            EntitySnapshotModel lastEntitySnapshot = entitySnapshotManager.getLastEntitySnapshot(selectedEntity);
         /*   EntitySnapshot snapshot = entitySnapshotService.getSnapshots(metadata.getClass(Agent.class), selectedEntity.getId())
                    .stream().dropWhile(entitySnapshot -> entitySnapshot.getChangeDate() == selectedEntity.getUpdateTs()).findFirst().get();*/
            boolean equals = false;
            if (lastEntitySnapshot != null && selectedEntity.getUpdateData() != null) {
                User agentSnapshot = (User) entitySnapshotManager.extractEntity(lastEntitySnapshot);

                equals = agentSnapshot != (selectedEntity);
            }
            userTableApprove.setVisible(!equals && canShowMenu);
            userTableDecline.setVisible(!equals && canShowMenu);
            userTableShowSnapshots.setVisible(equals && canShowMenu);
        });
    }

    private Supplier<Field> getMessageField() {
        return () -> {
            TextArea webTextArea =uiComponents.create(TextArea.class);
            webTextArea.setRows(7);
            return webTextArea;
        };
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        usersTable.setSelected(Collections.emptyList());
        usersDl.load();
    }
    @Subscribe("usersTable.approve")
    public void onAgentsTableApprove(Action.ActionPerformedEvent event) {
        dialogs.createOptionDialog().withMessage("Are sure you want to approve this record?").withCaption("Record approval confirmation!").withActions(new DialogAction(DialogAction.Type.YES).withHandler(e -> {

            //launch an input dialog for getting the message
            dialogs.createInputDialog(this).withCaption("Enter the approval message").withParameters(InputParameter.stringParameter("message").withField(getMessageField()).withCaption("Message").withRequired(true)

            ).withActions(DialogActions.OK_CANCEL).withCloseListener(closeEvent -> {
                if (closeEvent.closedWith(DialogOutcome.OK)) {
                    String message = closeEvent.getValue("message");

                    if (usersDc.getItemOrNull() == null) return;
                    User item = usersDc.getItem();

                    makerCheckerService.onApproveUpdate(item, true, message);
                    //reload and deselect
                    usersTable.setSelected(Collections.emptyList());
                    usersDl.load();

                }
            }).show();

        }), new DialogAction(DialogAction.Type.NO)).show();
    }
    @Subscribe("usersTable.decline")
    public void onAgentsTableDecline(Action.ActionPerformedEvent event) {
        dialogs.createOptionDialog().withMessage("Are sure you want to decline this record?").withCaption("Record approval confirmation!").withActions(new DialogAction(DialogAction.Type.YES).withHandler(e -> {


            //launch an input dialog for getting the message
            dialogs.createInputDialog(this).withCaption("Enter the decline reason").withParameters(InputParameter.stringParameter("message").withField(getMessageField()).withCaption("Decline Message").withRequired(true)).withActions(DialogActions.OK_CANCEL).withCloseListener(closeEvent -> {
                if (closeEvent.closedWith(DialogOutcome.OK)) {
                    String message = closeEvent.getValue("message");

                    if (usersDc.getItemOrNull() == null) return;
                    User item = usersDc.getItem();

                    makerCheckerService.onApproveUpdate(item, false, message);
                    //reload and deselect
                    usersTable.setSelected(Collections.emptyList());
                    usersDl.load();
                }
            }).show();
        }), new DialogAction(DialogAction.Type.NO)).show();
    }
    @Subscribe("usersTable.showSnapshots")
    public void onAgentsTableShowSnapshots(Action.ActionPerformedEvent event) {
        UserViewchangesScreen screen = screenBuilders.screen(this).withScreenClass(UserViewchangesScreen.class).withOpenMode(OpenMode.DIALOG).build();

        UUID id = usersDc.getItem().getId();
        screen.setEntityId(id);
        screen.setEntity(usersDc.getItem());
        screen.addAfterCloseListener(afterCloseEvent -> {
            usersTable.setSelected(Collections.emptyList());
            usersDl.load();
            //showSnapshotsBtn.setEnabled(true);
        });
        screen.show();
    }



}