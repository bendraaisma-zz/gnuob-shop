package com.netbrasoft.gnuob.shop.category;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.category.CategoryDataProvider;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CategoryHomePanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class CategoryDataview extends DataView<Category> {

    private static final String CONTENT_ID = "content";

    private static final String CONTENT_BORDER_CONTENT_BORDER_BODY_MAIN_MENU_PANEL_MAIN_MENU_TABBED_PANEL = "contentBorder:contentBorder_body:mainMenuPanel:mainMenuTabbedPanel";

    private static final String CLICK_EVENT = "click";

    private static final long serialVersionUID = 5098665993468197838L;

    private CategoryDataview(final String id, final IDataProvider<Category> dataProvider, final long itemsPerPage) {
      super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateItem(final Item<Category> item) {
      final AbstractReadOnlyModel<String> readOnlyModel = new AbstractReadOnlyModel<String>() {

        private static final long serialVersionUID = 4751535250171413561L;

        @Override
        public String getObject() {
          final StringBuilder stringBuilder = new StringBuilder();
          for (final Content content : item.getModelObject().getContents()) {
            stringBuilder.append(new String(content.getContent()));
          }
          return stringBuilder.toString();
        }
      };
      final AjaxEventBehavior ajaxEventBehavior = new AjaxEventBehavior(CLICK_EVENT) {

        private static final long serialVersionUID = 175594289469817897L;

        @Override
        public void onEvent(final AjaxRequestTarget target) {
          final BootstrapTabbedPanel<ITab> bootstrapTabbedPanel =
              (BootstrapTabbedPanel<ITab>) getPage().get(CONTENT_BORDER_CONTENT_BORDER_BODY_MAIN_MENU_PANEL_MAIN_MENU_TABBED_PANEL);

          for (final ITab tab : bootstrapTabbedPanel.getTabs()) {
            if (tab instanceof CategoryTab && ((CategoryTab) tab).getModelObject().getId() == ((Category) item.getDefaultModelObject()).getId()) {
              bootstrapTabbedPanel.setSelectedTab(bootstrapTabbedPanel.getTabs().lastIndexOf(tab));
            }
          }
          target.add(target.getPage());
        }
      };
      final Label contentLabel = new Label(CONTENT_ID, readOnlyModel);
      item.setModel(new CompoundPropertyModel<Category>(item.getModelObject()));
      item.add(contentLabel.setEscapeModelStrings(false));
      item.add(ajaxEventBehavior);
    }
  }

  private static final String CATEGORY_DATA_VIEW_ID = "categoryDataView";

  private static final long serialVersionUID = 5858682402634442147L;

  @SpringBean(name = CategoryDataProvider.CATEGORY_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeDataProvider<Category> categoryDataProvider;

  private final CategoryDataview categoryDataView;

  public CategoryHomePanel(final String id, final IModel<Category> model) {
    super(id, model);
    categoryDataView = new CategoryDataview(CATEGORY_DATA_VIEW_ID, categoryDataProvider, Integer.MAX_VALUE);
  }

  @Override
  protected void onInitialize() {
    categoryDataProvider.setUser(AppServletContainerAuthenticatedWebSession.getUserName());
    categoryDataProvider.setPassword(AppServletContainerAuthenticatedWebSession.getPassword());
    categoryDataProvider.setSite(AppServletContainerAuthenticatedWebSession.getSite());
    categoryDataProvider.setType(new Category());
    categoryDataProvider.getType().setActive(true);
    categoryDataProvider.setOrderBy(OrderBy.POSITION_A_Z);
    add(categoryDataView.setOutputMarkupId(true));
    super.onInitialize();
  }
}
