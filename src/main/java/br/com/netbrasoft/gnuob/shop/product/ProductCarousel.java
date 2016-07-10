package br.com.netbrasoft.gnuob.shop.product;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import br.com.netbrasoft.gnuob.shop.security.ShopRoles;

import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.Carousel;
import de.agilecoders.wicket.core.markup.html.bootstrap.carousel.ICarouselImage;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class ProductCarousel extends Carousel {

  private static final long serialVersionUID = -8356867197970835590L;

  public ProductCarousel(final String id, final IModel<List<ICarouselImage>> model) {
    super(id, model);
  }

  @Override
  protected Component newImage(final String markupId, final ICarouselImage image) {
    final Label htmlLabel = new Label(markupId, new AbstractReadOnlyModel<String>() {

      private static final long serialVersionUID = -7501719023515852494L;

      @Override
      public String getObject() {
        return image.url();
      }
    });
    return htmlLabel.setEscapeModelStrings(false);
  }
}
