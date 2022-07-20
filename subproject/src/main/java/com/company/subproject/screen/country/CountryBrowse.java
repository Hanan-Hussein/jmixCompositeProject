package com.company.subproject.screen.country;

import io.jmix.ui.screen.*;
import com.company.subproject.entity.Country;

@UiController("Country.browse")
@UiDescriptor("country-browse.xml")
@LookupComponent("countriesTable")
public class CountryBrowse extends StandardLookup<Country> {
}