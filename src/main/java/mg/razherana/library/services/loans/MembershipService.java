package mg.razherana.library.services.loans;

import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.repositories.loans.MembershipRepository;
import mg.razherana.library.repositories.loans.MembershipTypeRepository;
import mg.razherana.library.repositories.loans.PeopleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {

  @Autowired
  private MembershipRepository membershipRepository;

  @Autowired
  private PeopleRepository peopleRepository;

  @Autowired
  private MembershipTypeRepository membershipTypeRepository;

  public List<Membership> findAll() {
    return membershipRepository.findAllOrderByStartDateDesc();
  }

  public Membership findById(Long id) {
    return membershipRepository.findById(id).orElse(null);
  }

  public List<Membership> findByPeople(People people) {
    return membershipRepository.findByPeopleOrderByStartDateDesc(people);
  }

  public boolean hasActiveMembership(Long peopleId) {
    List<Membership> activeMemberships = membershipRepository.findActiveMembershipsByPeopleId(peopleId);
    return !activeMemberships.isEmpty();
  }

  @Transactional
  public Membership register(Long peopleId, Long membershipTypeId, LocalDate startDate, LocalDate endDate) {
    // Check if person already has an active membership
    if (hasActiveMembership(peopleId)) {
      throw new IllegalStateException("Person already has an active membership");
    }

    Optional<People> peopleOpt = peopleRepository.findById(peopleId);
    Optional<MembershipType> typeOpt = membershipTypeRepository.findById(membershipTypeId);

    if (peopleOpt.isPresent() && typeOpt.isPresent()) {
      Membership membership = new Membership();
      membership.setPeople(peopleOpt.get());
      membership.setMembershipType(typeOpt.get());
      membership.setStartDate(startDate);
      membership.setEndDate(endDate);

      return membershipRepository.save(membership);
    }

    return null;
  }
}
