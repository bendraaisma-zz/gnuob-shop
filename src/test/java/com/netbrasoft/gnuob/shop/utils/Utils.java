package com.netbrasoft.gnuob.shop.utils;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public final class Utils {

  public static Archive<?> createDeployment() {

    final PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");

    final JavaArchive[] wicketCore = pom.resolve("org.apache.wicket:wicket-core").withoutTransitivity().as(JavaArchive.class);
    final JavaArchive[] wicketExtentsion = pom.resolve("org.apache.wicket:wicket-extensions").withoutTransitivity().as(JavaArchive.class);
    final JavaArchive[] wicketStuff = pom.resolve("org.wicketstuff:wicketstuff-servlet3-auth").withTransitivity().as(JavaArchive.class);
    final JavaArchive[] wicketBootstrap = pom.resolve("de.agilecoders.wicket:wicket-bootstrap-core").withTransitivity().as(JavaArchive.class);
    final JavaArchive[] oauth2OidcSdk = pom.resolve("com.nimbusds:oauth2-oidc-sdk").withTransitivity().as(JavaArchive.class);

    return ShrinkWrap.create(WebArchive.class, "gnuob-test-application.war").addPackages(true, "com.netbrasoft.gnuob.shop").addPackages(true, "com.netbrasoft.gnuob.api")
        .addAsResource("META-INF/MANIFEST.MF", "META-INF/MANIFEST.MF").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
        .addAsLibraries(wicketCore, wicketExtentsion, oauth2OidcSdk, wicketStuff, wicketBootstrap);
  }
}
