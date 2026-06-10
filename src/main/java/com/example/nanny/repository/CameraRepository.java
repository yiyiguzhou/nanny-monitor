package com.example.nanny.repository;

import com.example.nanny.domain.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraRepository extends JpaRepository<Camera, String> {}
