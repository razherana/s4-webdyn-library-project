package mg.razherana.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.users.Access;
import mg.razherana.library.repositories.AccessRepository;

@Service
public class AccessService {

  @Autowired
  private AccessRepository accessRepository;

  public List<Access> findAll() {
    return accessRepository.findAll();
  }

  public Access findById(Long id) {
    return accessRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Access not found with id: " + id));
  }

  public Access save(String name, String uri, String methodType) {
    Access access = new Access();
    access.setName(name);
    access.setUri(uri);
    access.setMethodType(methodType);
    return accessRepository.save(access);
  }

  public Access update(Long id, String name, String uri, String methodType) {
    Access access = findById(id);
    access.setName(name);
    access.setUri(uri);
    access.setMethodType(methodType);
    return accessRepository.save(access);
  }

  public void delete(Long id) {
    accessRepository.deleteById(id);
  }
}
