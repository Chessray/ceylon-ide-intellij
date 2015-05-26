package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;

public class CeylonFacet extends Facet<CeylonFacetConfiguration> {

    public static final FacetTypeId<CeylonFacet> ID = new FacetTypeId<>("ceylon");

    public CeylonFacet(Module module, String name, CeylonFacetConfiguration configuration) {
        super(getFacetType(), module, name, configuration, null);
    }

    public static CeylonFacetType getFacetType() {
        return (CeylonFacetType) FacetTypeRegistry.getInstance().findFacetType(ID);
    }
}
