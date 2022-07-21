package com.company.subproject.screen.country;

import io.jmix.ui.screen.*;
import com.company.subproject.entity.Country;

@UiController("Country.edit")
@UiDescriptor("country-edit.xml")
@EditedEntityContainer("countryDc")
public class CountryEdit extends StandardEditor<Country> {
}