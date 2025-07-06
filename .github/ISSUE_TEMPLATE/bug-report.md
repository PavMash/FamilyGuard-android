name: "Bug Report"
description: "Report a bug with reproduction steps and expected behavior."
title: "[Bug]: "
labels: ["Bug", "Meta: To Triage"]
projects: ["inno-swp-2025/5"]
type: "issue"
body:
  - type: textarea
    id: summary
    attributes:
      label: "Summary"
      description: Briefly describe the bug.
    validations:
      required: true

  - type: textarea
    id: steps
    attributes:
      label: "Reproduction Steps"
      description: Provide clear steps to reproduce the bug.
      value: |
        1.
        2.
        3.
    validations:
      required: true

  - type: textarea
    id: expected
    attributes:
      label: "Expected Behavior"
      description: What should happen?
    validations:
      required: true

  - type: textarea
    id: actual
    attributes:
      label: "Actual Behavior"
      description: What actually happens?
    validations:
      required: true

  - type: textarea
    id: env
    attributes:
      label: "Environment"
      description: OS, Browser, App version, etc.
    validations:
      required: false
