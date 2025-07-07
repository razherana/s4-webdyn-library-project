package mg.razherana.library.services.loans;

import mg.razherana.library.models.loans.People;
import mg.razherana.library.repositories.loans.PeopleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PeopleService {

  @Autowired
  private PeopleRepository peopleRepository;

  public List<People> findAll() {
    return peopleRepository.findAllWithMemberships();
  }

  public People findById(Long id) {
    return peopleRepository.findById(id).orElse(null);
  }

  @Transactional
  public People save(String name, LocalDate birthDate, String address) {
    People people = new People();
    people.setName(name);
    people.setBirthDate(birthDate);
    people.setAddress(address);

    return peopleRepository.save(people);
  }

  public void save(People people) {
    peopleRepository.save(people);
  }
}
