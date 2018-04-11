
/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mike4christ.medmanager.Firebase;


public class FirebaseUserEntity {

    private String uId;

    private String email;

    private String name;

    private String country;

    private String phone;

    private String birthday;

    private String hobby;

    public FirebaseUserEntity(){
    }

    public FirebaseUserEntity(String uId, String email, String name, String country, String phone, String birthday, String hobby) {
        this.uId = uId;
        this.email = email;
        this.name = name;
        this.country = country;
        this.phone = phone;
        this.birthday = birthday;
        this.hobby = hobby;
    }

    public String getuId() {
        return uId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getHobby() {
        return hobby;
    }
}
