package com.company.subproject.screen.user;

import com.company.subproject.app.MakerCheckerService;
import com.company.subproject.entity.User;
import io.jmix.core.Metadata;
import io.jmix.core.Resources;
import io.jmix.ui.Dialogs;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.DialogAction;
import io.jmix.ui.action.list.CreateAction;

import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Field;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.function.Supplier;

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
    @Inject
    private MakerCheckerService makerCheckerService;
    @Autowired
    private Resources resources;

    public UserBrowse() {
    }

    @Subscribe
    public void onInit(InitEvent event) {

    }
    private Supplier<Field> getMessageField() {
        return () -> {
            TextArea webTextArea =uiComponents.create(TextArea.class);
            webTextArea.setRows(7);
            return webTextArea;
        };
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




}