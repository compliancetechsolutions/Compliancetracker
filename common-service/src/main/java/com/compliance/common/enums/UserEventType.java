package com.compliance.common.enums;

public enum UserEventType {

// =====================================================
// USER
// =====================================================

  CREATE_USER,

  UPDATE_USER,

  DELETE_USER,

// =====================================================
// BULK
// =====================================================

  BULK_CREATE_USERS,

// =====================================================
// ROLE
// =====================================================

  ADD_ROLE,

  ADD_MULTIPLE_ROLES,

  REMOVE_ROLE,

// =====================================================
// PASSWORD
// =====================================================

  RESET_PASSWORD,

// =====================================================
// STATUS
// =====================================================

  ACTIVATE_USER,

  DEACTIVATE_USER;

  public boolean isRoleOperation() {

    return

    this == ADD_ROLE

        ||

        this == ADD_MULTIPLE_ROLES

        ||

        this == REMOVE_ROLE;

  }

  public boolean isUserLifecycle() {

    return

    this == CREATE_USER

        ||

        this == UPDATE_USER

        ||

        this == DELETE_USER;

  }

}
