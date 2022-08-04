package com.company.subproject.screen.landlord;

import io.jmix.ui.screen.*;
import com.company.subproject.entity.Landlord;

@UiController("Landlord.browse")
@UiDescriptor("landlord-browse.xml")
@LookupComponent("landlordsTable")
public class LandlordBrowse extends StandardLookup<Landlord> {
}