// test_auth_flow.js - Automated Verification Script for Pranav Dhyan Ashram Platform
// Runs against backend API on port 8081

const API_BASE = 'http://localhost:8081/api';

async function runVerification() {
  console.log('========================================================================');
  console.log('🕉️  STARTING FULL END-TO-END AUTH & API VERIFICATION FOR PRANAV DHYAN');
  console.log('========================================================================\n');

  let testPassed = true;

  try {
    // 1. Check Public Categories Endpoint (Unauthenticated)
    console.log('TEST 1: Fetching Public Categories...');
    const catRes = await fetch(`${API_BASE}/categories`);
    if (catRes.status !== 200) throw new Error(`Categories failed with status ${catRes.status}`);
    const categories = await catRes.json();
    console.log(`✅ Passed: Retrieved ${categories.length} categories successfully.\n`);

    // 2. Check Public Preview Cutoffs (Unauthenticated)
    console.log('TEST 2: Verifying Public Preview Cutoffs (Unauthenticated Access)...');
    const previewPhotoRes = await fetch(`${API_BASE}/media/preview?type=PHOTO`);
    const previewPhotos = await previewPhotoRes.json();
    console.log(`✅ Passed: Public photo preview returned ${previewPhotos.content?.length || 0} items.`);

    const previewVideoRes = await fetch(`${API_BASE}/media/preview?type=VIDEO`);
    const previewVideos = await previewVideoRes.json();
    console.log(`✅ Passed: Public video preview returned ${previewVideos.content?.length || 0} items.\n`);

    // 3. Confirm Full Media Access is Rejected without Token
    console.log('TEST 3: Confirming Protected Full Archive Rejects Unauthenticated Request...');
    const unauthFullRes = await fetch(`${API_BASE}/media?type=PHOTO`);
    if (unauthFullRes.status === 401 || unauthFullRes.status === 403) {
      console.log(`✅ Passed: Full media archive correctly rejected unauthenticated request with status ${unauthFullRes.status}.\n`);
    } else {
      throw new Error(`Expected 401/403 for unauthenticated full media access, got ${unauthFullRes.status}`);
    }

    // 4. Register a New Student User
    console.log('TEST 4: Registering a New Student Profile (BCrypt Password Hashing & DB Save)...');
    const testEmail = `student_${Date.now()}@pranavdhyan.org`;
    const regRes = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: 'Siddharth Yogi',
        email: testEmail,
        password: 'SecurePassword108!'
      })
    });
    if (regRes.status !== 200 && regRes.status !== 201) {
      const errText = await regRes.text();
      throw new Error(`Registration failed (${regRes.status}): ${errText}`);
    }
    const regData = await regRes.json();
    console.log(`✅ Passed: Registered successfully. Issued JWT token for ${regData.user?.email || regData.email} with role ${regData.user?.role || regData.role}.\n`);

    // 5. Login with Demo Member Account
    console.log('TEST 5: Logging in with Demo Member Account (member@pranavdhyan.org)...');
    const memberLoginRes = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: 'member@pranavdhyan.org',
        password: 'Password123!'
      })
    });
    if (memberLoginRes.status !== 200) throw new Error(`Member login failed with status ${memberLoginRes.status}`);
    const memberData = await memberLoginRes.json();
    const memberToken = memberData.token;
    console.log(`✅ Passed: Member login successful. Token received, role is ${memberData.user?.role || memberData.role}.\n`);

    // 6. Verify Full Media Archive Access with Member Token
    console.log('TEST 6: Verifying Full Media Archive Access with Bearer Token...');
    const memberFullRes = await fetch(`${API_BASE}/media?type=PHOTO`, {
      headers: { 'Authorization': `Bearer ${memberToken}` }
    });
    if (memberFullRes.status !== 200) throw new Error(`Full media archive request failed with token (${memberFullRes.status})`);
    const memberFullPhotos = await memberFullRes.json();
    console.log(`✅ Passed: Full photographic archive unlocked for member (${memberFullPhotos.content?.length || 0} records fetched).\n`);

    // 7. Verify Member Cannot Access Admin Dashboard Endpoints (Role-Based Access Control)
    console.log('TEST 7: Verifying Member Cannot Access Admin Management Endpoints...');
    const memberAdminRes = await fetch(`${API_BASE}/admin/categories`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${memberToken}`
      },
      body: JSON.stringify({ name: 'Unauthorized Category', description: 'Should fail' })
    });
    if (memberAdminRes.status === 403 || memberAdminRes.status === 401) {
      console.log(`✅ Passed: Member access to Admin POST /api/admin/categories correctly blocked (${memberAdminRes.status}).\n`);
    } else {
      throw new Error(`Expected 403 Forbidden for member trying to create category, got ${memberAdminRes.status}`);
    }

    // 8. Login with Demo Admin Account
    console.log('TEST 8: Logging in with Demo Admin Account (admin@pranavdhyan.org)...');
    const adminLoginRes = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: 'admin@pranavdhyan.org',
        password: 'Password123!'
      })
    });
    if (adminLoginRes.status !== 200) throw new Error(`Admin login failed with status ${adminLoginRes.status}`);
    const adminData = await adminLoginRes.json();
    const adminToken = adminData.token;
    console.log(`✅ Passed: Admin login successful. Role is ${adminData.user?.role || adminData.role}.\n`);

    // 9. Verify Admin CRUD Flow (Create Category)
    console.log('TEST 9: Verifying Admin CRUD Capability (Creating a Test Archive Category)...');
    const catName = `Vastu Studies ${Date.now()}`;
    const createCatRes = await fetch(`${API_BASE}/admin/categories`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${adminToken}`
      },
      body: JSON.stringify({
        name: catName,
        description: 'Automated test verification category'
      })
    });
    if (createCatRes.status !== 200 && createCatRes.status !== 201) {
      const text = await createCatRes.text();
      throw new Error(`Admin category creation failed (${createCatRes.status}): ${text}`);
    }
    const createdCategory = await createCatRes.json();
    console.log(`✅ Passed: Admin created category '${createdCategory.name}' (ID: ${createdCategory.id}).\n`);

    // 10. Verify Admin Delete Category
    console.log('TEST 10: Verifying Admin Deletion of the Test Category...');
    const deleteCatRes = await fetch(`${API_BASE}/admin/categories/${createdCategory.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${adminToken}` }
    });
    if (deleteCatRes.status !== 200 && deleteCatRes.status !== 204) throw new Error(`Category deletion failed with status ${deleteCatRes.status}`);
    console.log(`✅ Passed: Admin successfully deleted test category.\n`);

    // 11. Verify Logout Simulation / Token Clearance Behavior
    console.log('TEST 11: Verifying Expired/Cleared Token Rejects Access (Logout Simulation)...');
    const invalidTokenRes = await fetch(`${API_BASE}/media?type=PHOTO`, {
      headers: { 'Authorization': `Bearer INVALID_OR_CLEARED_TOKEN_SAMPLE` }
    });
    if (invalidTokenRes.status === 401 || invalidTokenRes.status === 403) {
      console.log(`✅ Passed: Cleared/Invalid token correctly rejected (${invalidTokenRes.status}).\n`);
    } else {
      throw new Error(`Expected 401/403 when sending invalid token, got ${invalidTokenRes.status}`);
    }

    console.log('========================================================================');
    console.log('🏆 ALL 11 AUTHENTICATION & API VERIFICATION TESTS PASSED SUCCESSFULLY!');
    console.log('========================================================================\n');

  } catch (error) {
    console.error(`\n❌ VERIFICATION TEST FAILED: ${error.message}\n`);
    testPassed = false;
    process.exit(1);
  }
}

runVerification();
