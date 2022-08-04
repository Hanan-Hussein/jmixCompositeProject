package com.company.subproject.screen.landlord;

import io.jmix.ui.screen.*;
import com.company.subproject.entity.Landlord;

@UiController("Landlord.edit")
@UiDescriptor("landlord-edit.xml")
@EditedEntityContainer("landlordDc")
public class LandlordEdit extends StandardEditor<Landlord> {
}