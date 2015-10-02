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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.net.MediaType;
import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.api.Content;
import com.netbrasoft.gnuob.api.OrderBy;
import com.netbrasoft.gnuob.api.generic.GenericTypeDataProvider;
import com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CategoryHomePanel extends Panel {

   @AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
   class CategoryDataview extends DataView<Category> {

      private static final long serialVersionUID = 5098665993468197838L;

      private static final int ITEMS_PER_PAGE = 5;

      private CategoryDataview() {
         super("categoryDataView", categoryDataProvider, ITEMS_PER_PAGE);
      }

      @Override
      protected void populateItem(Item<Category> item) {
         item.setModel(new CompoundPropertyModel<Category>(item.getModelObject()));
         item.add(new Label("content", new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 4751535250171413561L;

            @Override
            public String getObject() {
               for (final Content content : item.getModelObject().getContents()) {

                  if (MediaType.HTML_UTF_8.is(MediaType.parse(content.getFormat()))) {
                     return new String(content.getContent());
                  }
               }
               return new String("");
            }
         }).setEscapeModelStrings(false));
         item.add(new AjaxEventBehavior("click") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(AjaxRequestTarget target) {
               final BootstrapTabbedPanel<ITab> bootstrapTabbedPanel = (BootstrapTabbedPanel<ITab>) getPage().get("contentBorder:contentBorder_body:mainMenuPanel:mainMenuTabbedPanel");

               for (final ITab tab : bootstrapTabbedPanel.getTabs()) {
                  if (tab instanceof CategoryTab && ((CategoryTab) tab).getModelObject().getId() == ((Category) item.getDefaultModelObject()).getId()) {
                     bootstrapTabbedPanel.setSelectedTab(bootstrapTabbedPanel.getTabs().lastIndexOf(tab));
                  }
               }

               target.add(target.getPage());
            }
         });
      }
   }

   private static final long serialVersionUID = 5858682402634442147L;

   @SpringBean(name = "CategoryDataProvider", required = true)
   private GenericTypeDataProvider<Category> categoryDataProvider;

   private final CategoryDataview categoryDataView;

   public CategoryHomePanel(final String id, final IModel<Category> model) {
      super(id, model);

      categoryDataView = new CategoryDataview();
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
