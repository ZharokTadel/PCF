package riquelme.ernesto.myapplicationtabbedactivity.objects;

public class ResourceLink extends Resource {

    /*
    CREATE TABLE pcf_link(
	id_link INT PRIMARY KEY, -- FOREIGN KEY id_resource
	url_link VARCHAR(500) NOT NULL
    );
     */
    private String urlLink;

    // CONSTRUCTORES
    public ResourceLink(String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, String urlLink) {
        super(titleResource, presentation, type, order, hidden, idUnit);
        this.urlLink = urlLink;
    }
/*
    public ResourceLink(int idResource, String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, String urlLink) {
        super(idResource, titleResource, presentation, type, order, hidden, idUnit);
        this.urlLink = urlLink;
    }
*/
    public ResourceLink(Resource resource, String urlLink) {
        super(resource);
        this.urlLink = urlLink;
    }

    // GETTERS && SETTERS
    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    @Override
    public void setIdUnit(int idUnit) {
        super.setIdUnit(idUnit);
    }

    @Override
    public int getIdUnit() {
        return super.getIdUnit();
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
    }

    @Override
    public boolean isHidden() {
        return super.isHidden();
    }

    @Override
    public void setOrder(int order) {
        super.setOrder(order);
    }

    @Override
    public int getOrder() {
        return super.getOrder();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public void setPresentation(String presentation) {
        super.setPresentation(presentation);
    }

    @Override
    public String getPresentation() {
        return super.getPresentation();
    }

    @Override
    public void setTitleResource(String titleResource) {
        super.setTitleResource(titleResource);
    }

    @Override
    public String getTitleResource() {
        return super.getTitleResource();
    }

    @Override
    public void setIdResource(int idResource) {
        super.setIdResource(idResource);
    }

    @Override
    public int getIdResource() {
        return super.getIdResource();
    }

}
