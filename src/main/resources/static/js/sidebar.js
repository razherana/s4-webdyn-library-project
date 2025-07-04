// Set active accordion item based on current page
document.addEventListener("DOMContentLoaded", function () {
  const currentPath = window.location.pathname;
  const navLinks = document.querySelectorAll(".sidebar .nav-link");

  // Find the active link
  let activeLink = null;
  navLinks.forEach((link) => {
    const href = link.getAttribute("href");
    if (href && currentPath.startsWith(href) && href !== "/") {
      // If we have a match that's not just the root
      if (!activeLink || href.length > activeLink.getAttribute("href").length) {
        // Take the longer match (more specific)
        activeLink = link;
      }
    }
  });

  if (activeLink) {
    // Mark the link as active
    activeLink.classList.add("active");

    // Find parent accordion and expand it
    const accordionBody = activeLink.closest(".accordion-collapse");
    if (accordionBody) {
      const accordionId = accordionBody.id;
      const accordionButton = document.querySelector(
        `[data-bs-target="#${accordionId}"]`
      );

      // Add active-parent class to the button
      if (accordionButton) {
        accordionButton.classList.add("active-parent");
      }

      // Create a new bootstrap.Collapse instance and show it
      const bsCollapse = new bootstrap.Collapse(accordionBody, {
        toggle: false,
      });
      bsCollapse.show();
    }
  }
});
