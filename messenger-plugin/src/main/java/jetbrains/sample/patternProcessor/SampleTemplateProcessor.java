/*
 * Copyright (c) 2000-2010 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.sample.patternProcessor;

import java.util.HashMap;
import java.util.Map;
import jetbrains.buildServer.notification.NotificationContext;
import jetbrains.buildServer.notification.TemplateProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Adds additional objects into notification templates' model.
 * After this plugin is iunstalled, template .ftl file can list users who will receive the notificaiton as
 * &lt;#list users as user&gt;${user.name},&lt;/#list&gt;
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 */
public class SampleTemplateProcessor implements TemplateProcessor {
  public SampleTemplateProcessor() {
  }

  @NotNull
  public Map<String, Object> fillModel(@NotNull NotificationContext context) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("users", context.getUsers());
    model.put("event", context.getEventType());
    return model;
  }
}
