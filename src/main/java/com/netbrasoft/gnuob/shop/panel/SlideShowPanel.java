package com.netbrasoft.gnuob.shop.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.api.Category;

public class SlideShowPanel extends Panel {

   public SlideShowPanel(final String id, final IModel<Category> model) {
      super(id, model);
   }

   private static final long serialVersionUID = 5442962435614721448L;

}
