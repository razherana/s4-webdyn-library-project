package mg.razherana.library.services.loans;

import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.repositories.loans.MembershipTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipTypeService {

  @Autowired
  private MembershipTypeRepository membershipTypeRepository;

  public List<MembershipType> findAll() {
    return membershipTypeRepository.findAll();
  }

  public MembershipType findById(Long id) {
    return membershipTypeRepository.findById(id).orElse(null);
  }

  @Transactional
  public MembershipType save(MembershipType membershipType) {
    return membershipTypeRepository.save(membershipType);
  }

  @Transactional
  public MembershipType update(Long id, String name, int maxBooksAllowedHome, int maxBooksAllowedLibrary) {
    Optional<MembershipType> optionalType = membershipTypeRepository.findById(id);

    if (optionalType.isPresent()) {
      MembershipType type = optionalType.get();
      type.setName(name);
      type.setMaxBooksAllowedHome(maxBooksAllowedHome);
      type.setMaxBooksAllowedLibrary(maxBooksAllowedLibrary);
      return membershipTypeRepository.save(type);
    }

    return null;
  }

  @Transactional
  public void delete(Long id) {
    membershipTypeRepository.deleteById(id);
  }
}
