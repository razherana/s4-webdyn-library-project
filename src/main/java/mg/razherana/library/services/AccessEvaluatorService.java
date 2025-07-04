package mg.razherana.library.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service("accessEvaluatorService") // Give it a bean name
public class AccessEvaluatorService {

  public Boolean hasAccess(boolean isAdmin, Set<String> userUris, String urlPattern) {
    if (isAdmin == Boolean.TRUE) {
      return true;
    }

    if (userUris == null) {
      return false;
    }

    try {
      for (String uri : userUris) {
        if (Pattern.matches(uri, urlPattern)) {
          return true;
        }
      }
    } catch (PatternSyntaxException e) {
      // Log the error, but fall back to simple contains check
      // You might want to log e.getMessage() here
      e.printStackTrace();
      for (String uri : userUris) {
        if (uri.startsWith(urlPattern)) {
          return true;
        }
      }
    }

    return false;
  }
}